package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.PasswordResetToken;
import cat.udl.eps.softarch.demo.domain.PasswordResetRateLimit;
import cat.udl.eps.softarch.demo.repository.PasswordResetTokenRepository;
import cat.udl.eps.softarch.demo.repository.PasswordResetRateLimitRepository;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.context.ApplicationEventPublisher;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokens;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final Clock clock;
    private final Validator validator;
    private final PasswordResetRateLimitRepository limits;
    private final ApplicationEventPublisher events;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(PasswordResetTokenRepository tokens, UserRepository users,
                                PasswordEncoder encoder, Clock clock, PasswordResetRateLimitRepository limits,
                                ApplicationEventPublisher events, Validator validator) {
        this.tokens = tokens;
        this.users = users;
        this.encoder = encoder;
        this.clock = clock;
        this.validator = validator;
        this.limits = limits;
        this.events = events;
    }

    @Transactional
    public void request(String email) {
        if (email == null || email.isBlank() || email.length() > 254
                || !validator.validateValue(EmailInput.class, "email", email).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        limits.lockByEmailHash("GLOBAL").orElseThrow();
        String emailHash = hash(normalized);
        PasswordResetRateLimit limit = limits.lockByEmailHash(emailHash)
                .orElseGet(() -> new PasswordResetRateLimit(emailHash, clock.instant()));
        limit.record(clock.instant());
        limits.save(limit);
        users.findByEmail(email.trim()).ifPresent(user -> {
            String token = issue(user);
            events.publishEvent(new PasswordResetMail(user.getEmail(), token));
        });
    }

    private static class EmailInput {
        @Email
        String email;
    }

    @Transactional
    public String issue(User user) {
        byte[] value = new byte[32];
        random.nextBytes(value);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(value);
        tokens.save(new PasswordResetToken(hash(token), user, clock.instant().plus(Duration.ofHours(1))));
        return token;
    }

    @Transactional
    public void validate(String token) {
        validToken(token, clock.instant());
    }

    @Transactional
    public void confirm(String token, String password) {
        Instant now = clock.instant();
        PasswordResetToken resetToken = validToken(token, now);
        User user = resetToken.getUser();
        if (password == null || password.isBlank() || password.length() < 8
                || password.length() > 256 || password.getBytes(StandardCharsets.UTF_8).length > 72
                || encoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }
        user.setPassword(encoder.encode(password));
        users.save(user);
        resetToken.markUsed(now);
    }

    private PasswordResetToken validToken(String token, Instant now) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }
        PasswordResetToken result = tokens.findForUpdate(hash(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.GONE, "RESET_TOKEN_EXPIRED"));
        if (!result.isValidAt(now)) {
            throw new ResponseStatusException(HttpStatus.GONE, "RESET_TOKEN_EXPIRED");
        }
        return result;
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
