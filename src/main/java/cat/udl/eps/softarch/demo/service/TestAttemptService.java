package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.AnswerAttempt;
import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerAttemptRepository;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Automatic grading of a submitted attempt (2.4.3) and leaderboard eligibility (2.4.2).
 * Everything is computed on the server; the client only changes the attempt status.
 */
@Service
public class TestAttemptService {

    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final AnswerAttemptRepository answerAttemptRepository;

    public TestAttemptService(QuestionRepository questionRepository,
                              AnswerOptionRepository answerOptionRepository,
                              AnswerAttemptRepository answerAttemptRepository) {
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.answerAttemptRepository = answerAttemptRepository;
    }

    /**
     * Grades the attempt and fills in score, counters, duration, submittedAt and
     * countsForLeaderboard. Called by the event handler right before the attempt is saved
     * with status SUBMITTED, so the entity is persisted by the caller afterwards.
     */
    @Transactional
    public void submit(TestAttempt attempt) {
        Instant now = Instant.now();
        Long testId = attempt.getTest().getId();

        List<Question> questions = questionRepository.listByTestId(testId);

        // question id -> id of its correct option
        Map<Long, Long> correctByQuestion = new HashMap<>();
        for (AnswerOption option : answerOptionRepository.listCorrectByTestId(testId)) {
            correctByQuestion.putIfAbsent(option.getQuestion().getId(), option.getId());
        }

        // question id -> answer already stored for this attempt
        Map<Long, AnswerAttempt> answers = new HashMap<>();
        for (AnswerAttempt answer : answerAttemptRepository.listByAttemptId(attempt.getId())) {
            answers.put(answer.getQuestion().getId(), answer);
        }

        int score = 0;
        int correct = 0;
        int incorrect = 0;
        int unanswered = 0;

        for (Question question : questions) {
            int points = question.getScore() == null ? 0 : question.getScore();
            AnswerAttempt answer = answers.get(question.getId());
            if (answer == null) {
                answer = new AnswerAttempt();
                answer.setAttempt(attempt);
                answer.setQuestion(question);
            }

            if (answer.getSelectedOption() == null) {
                // Unanswered: no option, zero points and no answer timestamp
                unanswered++;
                answer.setIsCorrect(false);
                answer.setAwardedScore(BigDecimal.ZERO);
                answer.setAnsweredAt(null);
            } else if (answer.getSelectedOption().getId().equals(correctByQuestion.get(question.getId()))) {
                correct++;
                score += points;
                answer.setIsCorrect(true);
                answer.setAwardedScore(BigDecimal.valueOf(points));
            } else {
                incorrect++;
                answer.setIsCorrect(false);
                answer.setAwardedScore(BigDecimal.ZERO);
            }
            answerAttemptRepository.save(answer);
        }

        attempt.setStatus(TestAttempt.Status.SUBMITTED);
        attempt.setSubmittedAt(now);
        attempt.setScore(score);
        attempt.setCorrectCount(correct);
        attempt.setIncorrectCount(incorrect);
        attempt.setUnansweredCount(unanswered);
        attempt.setDurationSeconds(attempt.getStartedAt() == null
                ? 0
                : (int) Duration.between(attempt.getStartedAt(), now).getSeconds());
        attempt.setCountsForLeaderboard(isEligibleForLeaderboard(attempt));
    }

    /**
     * Student, official test currently published and public, regular (non validation) attempt.
     * The attempt user and test cannot change once created (see TestAttemptEventHandler).
     */
    private boolean isEligibleForLeaderboard(TestAttempt attempt) {
        User user = attempt.getUser();
        Test test = attempt.getTest();
        return user != null
                && user.getRole() == User.Role.STUDENT
                && attempt.getAttemptType() == TestAttempt.AttemptType.REGULAR
                && test.getTestType() == Test.TestType.OFFICIAL
                && test.getStatus() == Test.Status.PUBLISHED
                && Boolean.TRUE.equals(test.getIsPublic());
    }
}
