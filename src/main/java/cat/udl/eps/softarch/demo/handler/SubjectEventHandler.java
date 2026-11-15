package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class SubjectEventHandler {
    final SubjectRepository subjectRepository;
    final TestRepository testRepository;
    final LeaderboardRepository leaderboardRepository;

    public SubjectEventHandler(SubjectRepository subjectRepository, TestRepository testRepository,
                               LeaderboardRepository leaderboardRepository) {
        this.subjectRepository = subjectRepository;
        this.testRepository = testRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    @HandleBeforeCreate
    public void handleSubjectPreCreate(Subject subject) {
        checkAdmin();
        trimName(subject);
        if (subject.getName() != null && subjectRepository.existsByNameIgnoreCase(subject.getName())) {
            throw nameAlreadyInUse();
        }
    }

    @HandleBeforeSave
    public void handleSubjectPreSave(Subject subject) {
        checkAdmin();
        trimName(subject);
        // The subject itself is excluded, so it can be saved keeping its current name
        if (subject.getName() != null
                && subjectRepository.existsByNameIgnoreCaseAndIdNot(subject.getName(), subject.getId())) {
            throw nameAlreadyInUse();
        }
    }

    // Deleting a subject with content would destroy its tests, results and leaderboards: it has to be archived
    @HandleBeforeDelete
    public void handleSubjectPreDelete(Subject subject) {
        checkAdmin();
        if (testRepository.existsBySubject(subject) || leaderboardRepository.existsBySubject(subject)) {
            throw new DataIntegrityViolationException("The subject has tests or results, archive it instead");
        }
    }

    private void checkAdmin() {
        if (!CurrentUser.isAdmin(CurrentUser.get())) {
            throw new AccessDeniedException("Only administrators can manage subjects");
        }
    }

    // Names are stored trimmed, so they can be compared ignoring the surrounding spaces
    private void trimName(Subject subject) {
        if (subject.getName() != null) {
            subject.setName(subject.getName().trim());
        }
    }

    private DataIntegrityViolationException nameAlreadyInUse() {
        return new DataIntegrityViolationException("Subject name already in use");
    }
}
