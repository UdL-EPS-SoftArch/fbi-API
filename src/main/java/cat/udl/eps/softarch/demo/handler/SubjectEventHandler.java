package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class SubjectEventHandler {
    final SubjectRepository subjectRepository;

    public SubjectEventHandler(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @HandleBeforeCreate
    public void handleSubjectPreCreate(Subject subject) {
        checkAdmin();
    }

    @HandleBeforeSave
    public void handleSubjectPreSave(Subject subject) {
        checkAdmin();
    }

    @HandleBeforeDelete
    public void handleSubjectPreDelete(Subject subject) {
        checkAdmin();
    }

    private void checkAdmin() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage subjects");
        }
    }
}
