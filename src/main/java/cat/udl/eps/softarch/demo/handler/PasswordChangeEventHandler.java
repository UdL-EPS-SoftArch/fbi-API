package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.PasswordChange;
import cat.udl.eps.softarch.demo.service.PasswordChangeService;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class PasswordChangeEventHandler {
    private final PasswordChangeService service;

    public PasswordChangeEventHandler(PasswordChangeService service) {
        this.service = service;
    }

    @HandleBeforeCreate
    public void change(PasswordChange command) {
        service.change(command.getCurrentPassword(), command.getNewPassword());
    }
}
