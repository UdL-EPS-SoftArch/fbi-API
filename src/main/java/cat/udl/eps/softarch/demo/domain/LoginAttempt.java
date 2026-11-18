package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Duration;
import java.time.Instant;

/**
 * Consecutive failed logins of an account (US-AUT-02): after MAX_FAILED_ATTEMPTS, new
 * attempts are rejected for LOCK_DURATION. Identified by a hash of the account, never by
 * the email itself.
 */
@Entity
public class LoginAttempt {
    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    @Id
    private String accountHash;
    private int failedCount;
    private Instant lockedUntil;

    protected LoginAttempt() {}

    public LoginAttempt(String accountHash) {
        this.accountHash = accountHash;
    }

    public boolean isLockedAt(Instant now) {
        return lockedUntil != null && now.isBefore(lockedUntil);
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void recordFailure(Instant now) {
        if (lockedUntil != null && !now.isBefore(lockedUntil)) {
            lockedUntil = null; // The previous lock has expired: start counting again
            failedCount = 0;
        }
        failedCount++;
        if (failedCount >= MAX_FAILED_ATTEMPTS) {
            lockedUntil = now.plus(LOCK_DURATION);
            failedCount = 0;
        }
    }
}
