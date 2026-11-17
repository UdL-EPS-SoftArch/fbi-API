package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.service.RateLimitExceededException;
import cat.udl.eps.softarch.demo.service.RateLimitService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Applies per-IP limits to reads, registration and password recovery, and limits failed
 * logins per IP and username. Runs inside the security chain, before authentication, so
 * blocked requests never reach the password check. Reports are limited in ReportEventHandler,
 * where the authenticated user is known.
 */
public class RateLimitFilter extends OncePerRequestFilter {
    private static final Set<String> RECOVERY_PATHS = Set.of(
            "/password-reset-requests", "/password-reset-validations", "/password-reset-confirmations");

    private final RateLimitService rateLimits;

    public RateLimitFilter(RateLimitService rateLimits) {
        this.rateLimits = rateLimits;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String method = request.getMethod();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String ip = request.getRemoteAddr();
        String username = basicUsername(request);
        String loginKey = ip + ":" + username;

        try {
            if ("GET".equals(method) || "HEAD".equals(method)) {
                rateLimits.consume("public", ip);
            } else if ("POST".equals(method) && "/users".equals(path)) {
                rateLimits.consume("registration", ip);
            } else if ("POST".equals(method) && RECOVERY_PATHS.contains(path)) {
                rateLimits.consume("recovery", ip);
            }
            if (username != null) {
                rateLimits.checkNotExhausted("login", loginKey);
            }
        } catch (RateLimitExceededException exceeded) {
            reject(response, exceeded.getRetryAfterSeconds());
            return;
        }

        chain.doFilter(request, response);

        // Only failed logins count, so a logged-in user sending credentials on every request is never blocked.
        if (username != null && response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            rateLimits.recordAttempt("login", loginKey);
        }
    }

    private static void reject(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"retryAfterSeconds\":"
                + retryAfterSeconds + "}");
    }

    /** Username from an HTTP Basic Authorization header, or null when there is none or it is malformed. */
    private static String basicUsername(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            return null;
        }
        try {
            String credentials = new String(Base64.getDecoder().decode(header.substring(6).trim()),
                    StandardCharsets.UTF_8);
            int colon = credentials.indexOf(':');
            return colon > 0 ? credentials.substring(0, colon) : null;
        } catch (IllegalArgumentException malformed) {
            return null;
        }
    }
}
