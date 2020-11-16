package engine.models;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class Answer {
    @NotNull
    private Set<Integer> answer;

    public Set<Integer> getAnswer() {
        return answer;
    }

    public void setAnswer(Set<Integer> answer) {
        this.answer = answer;
    }
}
