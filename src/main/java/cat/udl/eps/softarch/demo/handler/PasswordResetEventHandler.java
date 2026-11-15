package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.PasswordResetRequest;
import cat.udl.eps.softarch.demo.domain.PasswordResetValidation;
import cat.udl.eps.softarch.demo.domain.PasswordResetConfirmation;
import cat.udl.eps.softarch.demo.service.PasswordResetService;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class PasswordResetEventHandler {
    private final PasswordResetService service;

    public PasswordResetEventHandler(PasswordResetService service) {
        this.service = service;
    }

    @HandleBeforeCreate
    public void request(PasswordResetRequest command) {
        service.request(command.getEmail());
    }

    @HandleBeforeCreate
    public void validate(PasswordResetValidation command) {
        service.validate(command.getToken());
    }

    @HandleBeforeCreate
    public void confirm(PasswordResetConfirmation command) {
        service.confirm(command.getToken(), command.getNewPassword());
    }
}
