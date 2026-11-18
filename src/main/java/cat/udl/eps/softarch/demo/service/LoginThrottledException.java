package cat.udl.eps.softarch.demo.service;

import org.springframework.security.core.AuthenticationException;

import java.time.Instant;

/** Login rejected because of too many consecutive failed attempts (US-AUT-02). */
public class LoginThrottledException extends AuthenticationException {
    private final Instant lockedUntil;

    public LoginThrottledException(Instant lockedUntil) {
        super("TOO_MANY_LOGIN_ATTEMPTS");
        this.lockedUntil = lockedUntil;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }
}
