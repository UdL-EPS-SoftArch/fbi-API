package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class TestAttempt extends UriEntity<Long> {

    public enum AttemptType {
        REGULAR, VALIDATION
    }

    public enum Status {
        IN_PROGRESS, SUBMITTED, ABANDONED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Test test;

    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AttemptType attemptType = AttemptType.REGULAR;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_PROGRESS;

    // Results are computed by the server, never sent by the client
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer score;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer attemptNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer durationSeconds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer correctCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer incorrectCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer unansweredCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean countsForLeaderboard = false;

    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant startedAt;

    @DateTimeFormat
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant submittedAt;
}
