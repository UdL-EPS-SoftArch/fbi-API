package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class AttemptQuestion {
    private Long questionId;
    private Integer questionScore;
    private Long correctOptionId;
    private Long selectedOptionId;

    public AttemptQuestion(Long questionId, Integer questionScore, Long correctOptionId) {
        this.questionId = questionId;
        this.questionScore = questionScore;
        this.correctOptionId = correctOptionId;
    }
}
