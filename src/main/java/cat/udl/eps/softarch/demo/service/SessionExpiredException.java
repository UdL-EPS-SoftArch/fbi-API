package cat.udl.eps.softarch.demo.service;

import org.springframework.security.core.AuthenticationException;

/** The session token is unknown, expired or was revoked by logging out (US-AUT-06). */
public class SessionExpiredException extends AuthenticationException {
    public SessionExpiredException() {
        super("SESSION_EXPIRED");
    }
}
