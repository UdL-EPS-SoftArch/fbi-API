package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.EmailVerificationConfirmation;
import cat.udl.eps.softarch.demo.domain.EmailVerificationRequest;
import cat.udl.eps.softarch.demo.service.EmailVerificationService;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

/** Public commands of the email verification (US-AUT-07). */
@Component
@RepositoryEventHandler
public class EmailVerificationEventHandler {
    private final EmailVerificationService service;

    public EmailVerificationEventHandler(EmailVerificationService service) {
        this.service = service;
    }

    @HandleBeforeCreate
    public void request(EmailVerificationRequest command) {
        service.request(command.getEmail());
    }

    @HandleBeforeCreate
    public void confirm(EmailVerificationConfirmation command) {
        service.confirm(command.getToken());
    }
}
