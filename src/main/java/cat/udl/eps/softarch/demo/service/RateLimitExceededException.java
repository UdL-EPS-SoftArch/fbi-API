package cat.udl.eps.softarch.demo.service;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** 429 Too Many Requests carrying how long the client must wait, sent as Retry-After. */
@Getter
public class RateLimitExceededException extends ResponseStatusException {
    // Same body shape as the login errors (LoginFailureEntryPoint): status, code and message.
    public static final String CODE = "TOO_MANY_REQUESTS";
    public static final String MESSAGE = "Too many requests, please try again later";

    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds) {
        super(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        return headers;
    }
}
