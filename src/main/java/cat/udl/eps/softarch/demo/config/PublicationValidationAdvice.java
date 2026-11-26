package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.service.PublicationValidationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The 400 of a refused publication has the same body as GET /tests/{id}/publicationCheck, plus the
 * usual status, code and message, so errors returned by the server look like the checklist.
 */
@RestControllerAdvice
public class PublicationValidationAdvice {

    @ExceptionHandler(PublicationValidationException.class)
    public ResponseEntity<Map<String, Object>> handle(PublicationValidationException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("code", PublicationValidationException.CODE);
        body.put("message", exception.getReason());
        body.put("publishable", false);
        body.put("requirements", exception.getCheck().requirements());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
