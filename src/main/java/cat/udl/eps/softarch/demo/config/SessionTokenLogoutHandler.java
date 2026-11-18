package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/** Logging out (US-AUT-06) deletes the session token on the server, if the request carries one. */
@Component
public class SessionTokenLogoutHandler implements LogoutHandler {
    private final SessionService sessionService;

    public SessionTokenLogoutHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = SessionTokenAuthenticationFilter.bearerToken(request);
        if (token != null) {
            sessionService.revoke(token);
        }
    }
}
