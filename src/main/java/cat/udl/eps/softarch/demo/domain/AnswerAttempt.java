package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"attempt_id", "question_id"}))
@Data
@EqualsAndHashCode(callSuper = true)
public class AnswerAttempt extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private TestAttempt attempt;

    @NotNull
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Question question;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private AnswerOption selectedOption;

    // Grading is computed by the server, never sent by the client
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isCorrect;

    @Column(precision = 8, scale = 2)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal awardedScore;

    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant answeredAt;
}
