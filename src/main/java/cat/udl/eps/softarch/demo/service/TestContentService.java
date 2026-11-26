package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** A published official test must be public (2.4.4). Checked on every save of a published test. */
    public void checkVisibility(Test test) {
        if (test.getStatus() == Test.Status.PUBLISHED && !officialIsPublic(test).passed()) {
            throw new PublicationValidationException(PublicationCheck.of(List.of(officialIsPublic(test))));
        }
    }

    /** Throws a PublicationValidationException with the whole checklist when the test cannot be published. */
    public void checkPublishable(Test test) {
        PublicationCheck check = evaluate(test);
        if (!check.publishable()) {
            throw new PublicationValidationException(check);
        }
    }

    /**
     * Publication checklist (US-EST-23 rules, shown by US-EST-39): at least one question, every
     * question with at least two options and exactly one correct option, and an official test
     * must be public. Subject, title and non-negative scores are already enforced by bean validation.
     * Questions are numbered by their position in the test, starting at 1.
     */
    public PublicationCheck evaluate(Test test) {
        List<Question> questions = test.getId() == null
                ? List.of()
                : questionRepository.listByTestId(test.getId());
        Map<Long, QuestionRepository.QuestionOptionStats> stats = new HashMap<>();
        if (test.getId() != null) {
            questionRepository.listOptionStatsByTestId(test.getId())
                    .forEach(stat -> stats.put(stat.getQuestionId(), stat));
        }

        List<PublicationCheck.Requirement> requirements = new ArrayList<>();
        requirements.add(new PublicationCheck.Requirement(PublicationCheck.HAS_QUESTIONS, !questions.isEmpty(),
                questions.isEmpty() ? "A test needs at least one question to be published"
                                    : "The test has at least one question",
                new PublicationCheck.Target("TEST", "questions", null, testUri(test))));

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            QuestionRepository.QuestionOptionStats stat = stats.get(question.getId());
            long options = stat == null ? 0 : stat.getOptionCount();
            long correct = stat == null ? 0 : stat.getCorrectCount();
            int number = i + 1;
            PublicationCheck.Target target = new PublicationCheck.Target("QUESTION", null, number, question.getUri());

            requirements.add(new PublicationCheck.Requirement(PublicationCheck.MIN_OPTIONS, options >= 2,
                    "Question " + number + (options >= 2 ? " has at least two answer options"
                                                         : " needs at least two answer options"),
                    target));
            String correctMessage = correct == 0 ? " has no correct answer"
                    : correct > 1 ? " has more than one correct answer"
                    : " has exactly one correct answer";
            requirements.add(new PublicationCheck.Requirement(PublicationCheck.ONE_CORRECT_OPTION, correct == 1,
                    "Question " + number + correctMessage, target));
        }

        if (test.getTestType() == Test.TestType.OFFICIAL) {
            requirements.add(officialIsPublic(test));
        }
        return PublicationCheck.of(requirements);
    }

    private PublicationCheck.Requirement officialIsPublic(Test test) {
        boolean passed = test.getTestType() != Test.TestType.OFFICIAL || Boolean.TRUE.equals(test.getIsPublic());
        return new PublicationCheck.Requirement(PublicationCheck.OFFICIAL_PUBLIC, passed,
                passed ? "The official test is public" : "A published official test must be public",
                new PublicationCheck.Target("TEST", "isPublic", null, testUri(test)));
    }

    private String testUri(Test test) {
        return test.getId() == null ? null : test.getUri();
    }
}
