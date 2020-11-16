package engine.rest;

import engine.models.*;
import engine.repositories.CompletedQuizRepository;
import engine.repositories.QuizRepository;
import engine.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompletedQuizRepository completedQuizRepository;

    @GetMapping
    public Page<Quiz> getQuizzes(@RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.ASC, "id");
        return quizRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Quiz getQuiz(@PathVariable Long id) {
        return quizRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id = " + id));
    }

    @PostMapping(consumes = "application/json")
    public Quiz postQuiz(@Valid @RequestBody Quiz quiz, Authentication authentication) {
        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName()).orElse(null);
        }
        quiz.setAuthor(user);
        return quizRepository.save(quiz);
    }

    @PostMapping("/{id}/solve")
    public Feedback postAnswer(@PathVariable Long id, @Valid @RequestBody Answer answer, Authentication authentication) {
        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName()).orElse(null);
        }

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Quiz quiz = quizRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id = " + id));

        if (quiz.checkAnswer(answer.getAnswer())) {
            CompletedQuiz completedQuiz = new CompletedQuiz(quiz, user, new Date());
            completedQuizRepository.save(completedQuiz);
            return Feedback.RIGHT_FEEDBACK;
        } else {
            return Feedback.WRONG_FEEDBACK;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName()).orElse(null);
        }

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Quiz quiz = quizRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id = " + id));

        if (!user.getId().equals(quiz.getAuthor().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        quizRepository.delete(quiz);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("/completed")
    public Page<CompletedQuiz> completedQuizzes(@RequestParam(value = "page", defaultValue = "0") Integer page, Authentication authentication) {
        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName()).orElse(null);
        }

        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return completedQuizRepository.findByUser(user, PageRequest.of(page, 10, Sort.Direction.DESC, "completedAt"));
    }
}
