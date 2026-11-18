package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.LoginAttempt;
import cat.udl.eps.softarch.demo.repository.LoginAttemptRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Brute-force protection for the login (US-AUT-02): after 5 consecutive failed attempts on
 * an account, new attempts are rejected for 15 minutes, even with the right password.
 */
@Service
public class LoginThrottlingService {
    private final LoginAttemptRepository attempts;
    private final UserRepository users;
    private final Clock clock;

    public LoginThrottlingService(LoginAttemptRepository attempts, UserRepository users, Clock clock) {
        this.attempts = attempts;
        this.users = users;
        this.clock = clock;
    }

    /**
     * Key of the account a login identifier refers to: the username and the email of an
     * account share the same counter. Unknown identifiers are throttled too, so the
     * responses do not reveal which accounts exist. Returns null for a blank identifier.
     */
    @Transactional(readOnly = true)
    public String accountKey(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        String account = users.findByLoginIdentifier(identifier)
                .map(user -> "user:" + user.getId())
                .orElseGet(() -> "unknown:" + identifier.trim().toLowerCase(Locale.ROOT));
        return hash(account);
    }

    @Transactional(readOnly = true)
    public void checkNotThrottled(String accountKey) {
        Instant now = clock.instant();
        attempts.findById(accountKey)
                .filter(attempt -> attempt.isLockedAt(now))
                .ifPresent(attempt -> {
                    throw new LoginThrottledException(attempt.getLockedUntil());
                });
    }

    @Transactional
    public void recordFailure(String accountKey) {
        LoginAttempt attempt = attempts.lockByAccountHash(accountKey)
                .orElseGet(() -> new LoginAttempt(accountKey));
        attempt.recordFailure(clock.instant());
        attempts.save(attempt);
    }

    @Transactional
    public void recordSuccess(String accountKey) {
        if (attempts.existsById(accountKey)) {
            attempts.deleteById(accountKey);
        }
    }

    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
