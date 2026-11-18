package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.service.RateLimitExceededException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Gives limits reached inside Spring Data REST (reports, in ReportEventHandler) the same 429 body
 * as the ones RateLimitFilter rejects before reaching it.
 */
@RestControllerAdvice
public class RateLimitExceptionAdvice {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handle(RateLimitExceededException exceeded) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("code", RateLimitExceededException.CODE);
        body.put("message", RateLimitExceededException.MESSAGE);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(exceeded.getRetryAfterSeconds()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
