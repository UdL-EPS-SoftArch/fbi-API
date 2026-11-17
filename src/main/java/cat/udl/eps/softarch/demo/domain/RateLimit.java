package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Request counter for one rate limit key, such as {@code REGISTRATION:203.0.113.7}.
 * Internal entity: its repository is not exported through Spring Data REST.
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RateLimit extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String rateKey;

    private Instant windowStart;

    private int requestCount;

    public RateLimit(String rateKey, Instant now) {
        this.rateKey = rateKey;
        this.windowStart = now;
    }
}
