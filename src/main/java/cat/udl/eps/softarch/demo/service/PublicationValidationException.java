package cat.udl.eps.softarch.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** A test cannot be published; carries the whole checklist so the client can show every failure. */
public class PublicationValidationException extends ResponseStatusException {

    public static final String CODE = "PUBLICATION_VALIDATION_FAILED";

    private final transient PublicationCheck check;

    public PublicationValidationException(PublicationCheck check) {
        super(HttpStatus.BAD_REQUEST, String.join("; ",
                check.failed().stream().map(PublicationCheck.Requirement::message).toList()));
        this.check = check;
    }

    public PublicationCheck getCheck() {
        return check;
    }
}
