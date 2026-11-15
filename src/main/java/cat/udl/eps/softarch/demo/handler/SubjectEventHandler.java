package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
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
        if (subject.getName() != null) {
            subject.setName(subject.getName().trim());
            if (subjectRepository.existsByNameIgnoreCase(subject.getName())) {
                throw new DataIntegrityViolationException("Subject name already in use");
            }
        }
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
        if (!CurrentUser.isAdmin(CurrentUser.get())) {
            throw new AccessDeniedException("Only administrators can manage subjects");
        }
    }
}
