package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Rules about the content of a test: its derived maximum score (2.4.3) and the minimum
 * validation required to publish it (2.4.4).
 */
@Service
public class TestContentService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    public TestContentService(TestRepository testRepository, QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
    }

    /** TEST.max_score is the sum of the scores of its questions; called whenever they change. */
    public void recalculateMaxScore(Long testId) {
        if (testId == null) {
            return;
        }
        long sum = questionRepository.sumScoresByTestId(testId);
        testRepository.updateMaxScore(testId, (int) sum);
    }

    /** A published official test must be public (2.4.4). Checked on every create and save. */
    public void checkVisibility(Test test) {
        if (test.getTestType() == Test.TestType.OFFICIAL
                && test.getStatus() == Test.Status.PUBLISHED
                && !Boolean.TRUE.equals(test.getIsPublic())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A published official test must be public");
        }
    }

    /**
     * Minimum validation before publishing (PROPOSED policy in 2.4.4, pending team approval):
     * at least one question, and every question with at least two options and exactly one
     * correct option. Subject, title and non-negative scores are already enforced by bean validation.
     */
    public void checkPublishable(Test test) {
        List<QuestionRepository.QuestionOptionStats> questions = test.getId() == null
                ? List.of()
                : questionRepository.listOptionStatsByTestId(test.getId());

        List<String> problems = new ArrayList<>();
        if (questions.isEmpty()) {
            problems.add("A test needs at least one question to be published");
        }
        for (QuestionRepository.QuestionOptionStats question : questions) {
            if (question.getOptionCount() < 2) {
                problems.add("Question " + question.getQuestionId() + " needs at least two answer options");
            }
            if (question.getCorrectCount() != 1) {
                problems.add("Question " + question.getQuestionId() + " must have exactly one correct option");
            }
        }
        if (!problems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join("; ", problems));
        }
    }
}
