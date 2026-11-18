package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.QuestionReorder;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RepositoryEventHandler
public class QuestionReorderEventHandler {
    private final QuestionRepository questions;
    private final TestRepository tests;

    public QuestionReorderEventHandler(QuestionRepository questions, TestRepository tests) {
        this.questions = questions;
        this.tests = tests;
    }

    @HandleBeforeCreate
    public void reorder(QuestionReorder command) {
        if (command.getTest() == null || command.getTest().getId() == null || command.getQuestionIds() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test and question IDs are required");
        }
        Test test = tests.lockForContentChange(command.getTest().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test not found"));
        User user = CurrentUser.get();
        if (!CurrentUser.isAdmin(user) && (test.getTestType() == Test.TestType.OFFICIAL
                || !user.getUsername().equals(CurrentUser.usernameOf(test.getAuthor())))) {
            throw new AccessDeniedException("Only the test author can manage its questions");
        }
        List<Question> existing = questions.listByTestId(test.getId());
        List<Long> ids = command.getQuestionIds();
        Map<Long, Question> byId = new HashMap<>();
        existing.forEach(question -> byId.put(question.getId(), question));
        if (ids.size() != existing.size() || new HashSet<>(ids).size() != ids.size()
                || !byId.keySet().equals(new HashSet<>(ids))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question IDs must contain every question exactly once");
        }
        for (int index = 0; index < ids.size(); index++) {
            byId.get(ids.get(index)).setOrderIndex(index);
        }
        questions.saveAll(existing);
        command.setTest(null);
    }
}
