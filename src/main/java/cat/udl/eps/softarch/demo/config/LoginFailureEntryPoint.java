package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.service.LoginThrottledException;
import cat.udl.eps.softarch.demo.service.SessionExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;

/**
 * Login error responses (US-AUT-02): a JSON body with a stable code for the frontend and a
 * message for the user. Requests without credentials keep the plain empty 401 challenge.
 */
@Component
public class LoginFailureEntryPoint implements AuthenticationEntryPoint {
    static final String REALM = "demo";

    private final BasicAuthenticationEntryPoint challenge = new BasicAuthenticationEntryPoint();
    private final Clock clock;

    public LoginFailureEntryPoint(Clock clock) {
        this.clock = clock;
        challenge.setRealmName(REALM);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        if (exception instanceof LoginThrottledException throttled) {
            long seconds = Math.max(1, Duration.between(clock.instant(), throttled.getLockedUntil()).toSeconds());
            response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(seconds));
            write(response, HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_LOGIN_ATTEMPTS",
                    "Too many failed login attempts, please try again in a few minutes");
        } else if (exception instanceof SessionExpiredException) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"" + REALM + "\"");
            write(response, HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED",
                    "Your session has ended, please log in again");
        } else if (exception instanceof LockedException) {
            write(response, HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED",
                    "Your account is suspended, contact the administrators for more information");
        } else if (exception instanceof DisabledException) {
            write(response, HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED",
                    "Please verify your email address before logging in");
        } else if (exception instanceof BadCredentialsException) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + REALM + "\"");
            write(response, HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Incorrect email or password");
        } else {
            challenge.commence(request, response, exception);
        }
    }

    private static void write(HttpServletResponse response, HttpStatus status, String code, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // Fixed messages without quotes or backslashes: no JSON escaping needed
        response.getWriter().write("{\"status\":" + status.value() + ",\"code\":\"" + code
                + "\",\"message\":\"" + message + "\"}");
    }
}
