package engine.repositories;

import engine.models.CompletedQuiz;
import engine.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompletedQuizRepository extends PagingAndSortingRepository<CompletedQuiz, Long> {
    Page<CompletedQuiz> findByUser(User user, Pageable pageable);
}
