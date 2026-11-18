package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class AnswerOptionEventHandler {
    final AnswerOptionRepository answerOptionRepository;

    public AnswerOptionEventHandler(AnswerOptionRepository answerOptionRepository) {
        this.answerOptionRepository = answerOptionRepository;
    }

    @HandleBeforeCreate
    public void handleAnswerOptionPreCreate(AnswerOption option) {
        // The question is an existing entity referenced by the request, not modified by it
        checkTargetQuestion(option.getQuestion(), CurrentUser.get());
    }

    @HandleBeforeSave
    public void handleAnswerOptionPreSave(AnswerOption option) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: the current owner is
        // read from the database, and the (possibly new) question is checked as well.
        AnswerOptionRepository.OptionSnapshot stored = answerOptionRepository.findSnapshotById(option.getId())
                .orElseThrow(() -> new AccessDeniedException("Answer option not found"));

        if (!CurrentUser.isAdmin(user) && !user.getUsername().equals(stored.getTestAuthorUsername())) {
            throw new AccessDeniedException("Only the test author can manage its answer options");
        }
        checkTargetQuestion(option.getQuestion(), user);
    }

    // A question has a single correct answer: marking an option unmarks the previous one.
    // It runs after saving because a new option has no id before (QuestionTransactionFilter
    // keeps both writes in the request transaction).
    @HandleAfterCreate
    public void handleAnswerOptionPostCreate(AnswerOption option) {
        unmarkOtherCorrectOptions(option);
    }

    @HandleAfterSave
    public void handleAnswerOptionPostSave(AnswerOption option) {
        unmarkOtherCorrectOptions(option);
    }

    private void unmarkOtherCorrectOptions(AnswerOption option) {
        if (Boolean.TRUE.equals(option.getIsCorrect()) && option.getQuestion() != null) {
            answerOptionRepository.unmarkOtherCorrectOptions(option.getQuestion().getId(), option.getId());
        }
    }

    @HandleBeforeDelete
    public void handleAnswerOptionPreDelete(AnswerOption option) {
        checkTargetQuestion(option.getQuestion(), CurrentUser.get());
    }

    private void checkTargetQuestion(Question question, User user) {
        if (question == null) {
            return; // Rejected by @NotNull validation
        }
        String authorUsername = question.getTest() == null ? null : CurrentUser.usernameOf(question.getTest().getAuthor());
        if (!CurrentUser.isAdmin(user) && !user.getUsername().equals(authorUsername)) {
            throw new AccessDeniedException("Only the test author can manage its answer options");
        }
    }
}
