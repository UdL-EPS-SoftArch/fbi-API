package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Leaderboard extends UriEntity<Long> {

    public enum LeaderboardType {
        SUBJECT, GLOBAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    // Not used in the initial release (no test-specific leaderboards)
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Test test;

    // Only for SUBJECT entries, null for GLOBAL ones
    @ManyToOne
    @JsonIdentityReference(alwaysAsId = true)
    private Subject subject;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LeaderboardType leaderboardType;

    private Integer score;

    @Column(name = "rank_position")
    private Integer rank;

    @DateTimeFormat
    private Instant calculatedAt;
}
