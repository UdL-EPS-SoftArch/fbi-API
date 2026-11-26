package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.SessionToken;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.SessionTokenRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Server-side sessions (US-AUT-06): a token is issued after logging in with the credentials,
 * sent as "Authorization: Bearer <token>" and deleted when the user logs out.
 */
@Service
public class SessionService {
    private final SessionTokenRepository sessions;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public SessionService(SessionTokenRepository sessions, Clock clock) {
        this.sessions = sessions;
        this.clock = clock;
    }

    public record IssuedSession(String token, Instant expiresAt) {
    }

    @Transactional
    public IssuedSession issue(User user) {
        byte[] value = new byte[32];
        random.nextBytes(value);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(value);
        SessionToken session = sessions.save(new SessionToken(hash(token), user, clock.instant()));
        return new IssuedSession(token, session.getExpiresAt());
    }

    /** The user of a valid session; the account status is checked again on every request. */
    // An expired token is deleted even though the call fails
    @Transactional(noRollbackFor = SessionExpiredException.class)
    public User authenticate(String token) {
        String tokenHash = hash(token);
        SessionToken session = sessions.findByTokenHash(tokenHash)
                .orElseThrow(SessionExpiredException::new);
        if (!session.isValidAt(clock.instant())) {
            sessions.deleteByTokenHash(tokenHash);
            throw new SessionExpiredException();
        }
        User user = session.getUser();
        if (!user.isAccountNonLocked()) {
            throw new LockedException("ACCOUNT_SUSPENDED");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("EMAIL_NOT_VERIFIED");
        }
        return user;
    }

    /** Logging out: the token stops working immediately. Unknown tokens are ignored. */
    @Transactional
    public void revoke(String token) {
        sessions.deleteByTokenHash(hash(token));
    }

    private static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
