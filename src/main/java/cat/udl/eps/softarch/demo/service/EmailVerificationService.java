package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.EmailVerificationToken;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.EmailVerificationTokenRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Email verification (US-AUT-07): new accounts receive a link valid for 24 hours, can ask for a
 * new one, and are deleted if they are still unverified after 7 days.
 */
@Service
public class EmailVerificationService {
    public static final Duration UNVERIFIED_RETENTION = Duration.ofDays(7);
    static final String LINK_EXPIRED = "VERIFICATION_LINK_EXPIRED";

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    private final EmailVerificationTokenRepository tokens;
    private final UserRepository users;
    private final Clock clock;
    private final ApplicationEventPublisher events;
    private final RateLimitService rateLimits;
    private final Validator validator;
    private final SecureRandom random = new SecureRandom();

    public EmailVerificationService(EmailVerificationTokenRepository tokens, UserRepository users, Clock clock,
                                    ApplicationEventPublisher events, RateLimitService rateLimits,
                                    Validator validator) {
        this.tokens = tokens;
        this.users = users;
        this.clock = clock;
        this.events = events;
        this.rateLimits = rateLimits;
        this.validator = validator;
    }

    /** Sends a new link to the user; the previous links stop working. */
    @Transactional
    public void sendVerification(User user) {
        tokens.deleteByUser(user);
        byte[] value = new byte[32];
        random.nextBytes(value);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(value);
        tokens.save(new EmailVerificationToken(hash(token), user, clock.instant()));
        events.publishEvent(new EmailVerificationMail(user.getEmail(), token));
    }

    /**
     * Resends the link. The answer is the same whether the email is registered or not, so it
     * does not reveal which accounts exist; verified accounts receive nothing.
     */
    @Transactional
    public void request(String email) {
        if (email == null || email.isBlank() || email.length() > 254
                || !validator.validateValue(EmailInput.class, "email", email).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }
        String trimmed = email.trim();
        rateLimits.consume("verification-email", hash(trimmed.toLowerCase(Locale.ROOT)));
        users.findByEmail(trimmed)
                .filter(user -> !user.isEmailVerified())
                .ifPresent(this::sendVerification);
    }

    private static class EmailInput {
        @Email
        String email;
    }

    /** Opens the link: the account becomes verified and the link cannot be used again. */
    @Transactional
    public void confirm(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }
        Instant now = clock.instant();
        EmailVerificationToken verification = tokens.findForUpdate(hash(token))
                .filter(candidate -> candidate.isValidAt(now))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE, LINK_EXPIRED));
        User user = verification.getUser();
        user.setEmailVerified(true);
        users.save(user);
        tokens.deleteByUser(user);
    }

    /**
     * Deletes the accounts still unverified 7 days after registering. Each account is deleted
     * on its own, so one that cannot be deleted does not stop the others.
     */
    @Scheduled(cron = "${email-verification.cleanup-cron}")
    public void deleteStaleUnverifiedAccounts() {
        Instant cutoff = clock.instant().minus(UNVERIFIED_RETENTION);
        for (User user : users.findByEmailVerifiedFalseAndCreatedAtBefore(cutoff)) {
            try {
                users.deleteById(user.getId());
                logger.info("Deleted unverified account {}", user.getId());
            } catch (DataIntegrityViolationException exception) {
                logger.warn("Unable to delete unverified account {}", user.getId(), exception);
            }
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
