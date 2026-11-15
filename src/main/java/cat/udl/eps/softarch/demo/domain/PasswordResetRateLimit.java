package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class PasswordResetRateLimit {
    @Id
    private String emailHash;
    private Instant windowStart;
    private int requestCount;

    protected PasswordResetRateLimit() {}

    public PasswordResetRateLimit(String emailHash, Instant now) {
        this.emailHash = emailHash;
        windowStart = now;
    }

    public void record(Instant now) {
        if (!now.isBefore(windowStart.plusSeconds(900))) {
            windowStart = now;
            requestCount = 0;
        }
        if (requestCount >= 5) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
        }
        requestCount++;
    }
}
