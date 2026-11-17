package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;

/**
 * Request counter for one rate limit key, such as {@code REGISTRATION:203.0.113.7}.
 * Internal entity: it is never exposed through Spring Data REST.
 */
@Entity
public class RateLimit {
    @Id
    private String id;
    private Instant windowStart;
    private int requestCount;

    protected RateLimit() {}

    public RateLimit(String id, Instant now) {
        this.id = id;
        this.windowStart = now;
    }
}
