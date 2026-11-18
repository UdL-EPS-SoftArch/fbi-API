package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.*;
import cat.udl.eps.softarch.demo.repository.*;
import cat.udl.eps.softarch.demo.service.LeaderboardService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ManageQuestionsStepDefs {
    private final StepDefs steps;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final QuestionRepository questions;
    private final AnswerOptionRepository options;
    private final TestAttemptRepository attempts;
    private final AnswerAttemptRepository answers;
    private final ReportRepository reports;
    private final LeaderboardService leaderboards;
    private final JdbcTemplate jdbc;

    private Test test;
    private Question question;
    private Long reportId;
    private int previousCount;
    private Long correctAttempt;
    private Long incorrectAttempt;
    private Long unansweredAttempt;
    private final java.util.Map<Long, TestAttempt> savedAttempts = new java.util.HashMap<>();
    private Integer originalGlobalScore;
    private final Instant started = Instant.parse("2025-01-01T10:00:00Z");
    private final Instant submitted = Instant.parse("2025-01-01T10:05:00Z");

    public ManageQuestionsStepDefs(StepDefs steps, UserRepository users, SubjectRepository subjects,
            TestRepository tests, QuestionRepository questions, AnswerOptionRepository options,
            TestAttemptRepository attempts, AnswerAttemptRepository answers, ReportRepository reports,
            LeaderboardService leaderboards, JdbcTemplate jdbc) {
        this.steps = steps;
        this.users = users;
        this.subjects = subjects;
        this.tests = tests;
        this.questions = questions;
        this.options = options;
        this.attempts = attempts;
        this.answers = answers;
        this.reports = reports;
        this.leaderboards = leaderboards;
        this.jdbc = jdbc;
    }

    @Given("a {word} draft question test owned by {string} with {int} questions")
    public void draftTest(String type, String owner, int count) {
        Subject subject = new Subject();
        subject.setName("Question subject " + UUID.randomUUID());
        subject = subjects.save(subject);
        test = new Test();
        test.setSubject(subject);
        test.setAuthor(users.findByUsername(owner).orElseThrow());
        test.setTitle("Question test " + UUID.randomUUID());
        test.setTestType(Test.TestType.valueOf(type));
        test = tests.save(test);
        addExistingQuestions(count);
    }

    @Given("the question test has {int} questions")
    public void addExistingQuestions(int count) {
        int current = questions.listByTestId(test.getId()).size();
        for (int i = current; i < count; i++) {
            Question item = new Question();
            item.setTest(test);
            item.setQuestionText("Existing " + i);
            item.setOrderIndex(i);
            item.setScore(2);
            item = questions.save(item);
            if (question == null) question = item;
        }
        tests.updateMaxScore(test.getId(), count * 2);
    }

    @Given("the question actor is {string}")
    public void actor(String actor) {
        if (actor.equals("anonymous")) {
            AuthenticationStepDefs.currentUsername = null;
            AuthenticationStepDefs.currentPassword = null;
            return;
        }
        if (actor.equals("other")) {
            String username = "question-other";
            if (users.findByUsername(username).isEmpty()) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(username + "@example.test");
                user.setPassword("password");
                user.encodePassword();
                users.save(user);
            }
            actor = username;
        }
        AuthenticationStepDefs.currentUsername = actor;
        AuthenticationStepDefs.currentPassword = "password";
    }

    @When("I POST a question with statement {string}")
    public void postQuestion(String text) throws Exception {
        postBody(new JSONObject().put("test", test.getUri()).put("questionText", text));
    }

    @When("I POST a question with statement kind {string}")
    public void postInvalid(String kind) throws Exception {
        JSONObject body = new JSONObject().put("test", test.getUri());
        if (!kind.equals("missing")) body.put("questionText", kind.equals("empty") ? "" : "   ");
        postBody(body);
    }

    private void postBody(JSONObject body) throws Exception {
        previousCount = questions.listByTestId(test.getId()).size();
        perform(post("/questions"), body);
    }

    @When("I {word} the existing question")
    public void manage(String method) throws Exception {
        if (method.equals("POST")) {
            postQuestion("New question");
            return;
        }
        JSONObject body = new JSONObject().put("questionText", "Updated question").put("score", 4)
                .put("test", test.getUri()).put("questionType", "SINGLE_CHOICE");
        perform(request(method), body);
    }

    @When("I {word} the existing question with an empty statement")
    public void editEmpty(String method) throws Exception {
        perform(request(method), new JSONObject().put("questionText", "").put("test", test.getUri()));
    }

    @When("I PATCH the existing question score to {int}")
    public void changeScore(int score) throws Exception {
        perform(patch(question.getUri()), new JSONObject().put("score", score));
    }

    @When("I move the existing question to another test")
    public void moveQuestion() throws Exception {
        Test other = new Test();
        other.setSubject(test.getSubject());
        other.setAuthor(test.getAuthor());
        other.setTitle("Other " + UUID.randomUUID());
        other = tests.save(other);
        perform(patch(question.getUri()), new JSONObject().put("test", other.getUri()));
    }

    private MockHttpServletRequestBuilder request(String method) {
        return switch (method) {
            case "PUT" -> put(question.getUri());
            case "PATCH" -> patch(question.getUri());
            case "DELETE" -> delete(question.getUri());
            default -> throw new IllegalArgumentException(method);
        };
    }

    private void perform(MockHttpServletRequestBuilder request, JSONObject body) throws Exception {
        steps.result = steps.mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body.toString())
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Then("the question test still has {int} questions")
    public void countQuestions(int count) {
        assertEquals(count, questions.listByTestId(test.getId()).size());
    }

    @Then("the new question has default values and follows the existing questions")
    public void newQuestionDefaults() {
        List<Question> all = questions.listByTestId(test.getId());
        Question created = all.stream().filter(q -> q.getQuestionText().startsWith("New question")
                || q.getQuestionText().equals("Question 101")).findFirst().orElseThrow();
        assertEquals(previousCount, created.getOrderIndex());
        assertEquals(1, created.getScore());
        assertEquals(Question.QuestionType.SINGLE_CHOICE, created.getQuestionType());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM answer_option WHERE question_id = ?", Integer.class, created.getId()));
    }

    @Then("the question operation {string} is persisted")
    public void operationPersisted(String method) {
        List<Question> all = questions.listByTestId(test.getId());
        if (method.equals("POST")) {
            assertEquals(previousCount + 1, all.size());
        } else if (method.equals("DELETE")) {
            assertTrue(all.stream().noneMatch(q -> q.getId().equals(question.getId())));
            assertEquals(0, maxScore());
        } else {
            Question updated = all.stream().filter(q -> q.getId().equals(question.getId())).findFirst().orElseThrow();
            assertEquals("Updated question", updated.getQuestionText());
            assertEquals(4, updated.getScore());
            assertEquals(4, maxScore());
        }
    }

    @Then("the question request is denied")
    public void denied() {
        int status = steps.result.andReturn().getResponse().getStatus();
        assertTrue(status == 400 || status == 401 || status == 403 || status == 404, "Expected access denial, got " + status);
    }

    @Then("the original question remains unchanged")
    public void unchanged() {
        List<Question> all = questions.listByTestId(test.getId());
        assertEquals(1, all.size());
        assertEquals("Existing 0", all.getFirst().getQuestionText());
        assertEquals(2, all.getFirst().getScore());
        assertEquals(2, maxScore());
    }

    private int maxScore() {
        return jdbc.queryForObject("SELECT max_score FROM test WHERE id = ?", Integer.class, test.getId());
    }

    @Given("a published {word} question test with submitted attempts")
    public void publishedTest(String type) {
        draftTest(type, type.equals("OFFICIAL") ? "admin" : "demo", 1);
        test.setStatus(Test.Status.PUBLISHED);
        test.setIsPublic(true);
        tests.save(test);
        AnswerOption correct = option("Correct", true);
        AnswerOption incorrect = option("Incorrect", false);
        correctAttempt = attempt(correct, true, 1);
        incorrectAttempt = attempt(incorrect, false, 2);
        unansweredAttempt = attempt(null, false, 3);
        if (type.equals("OFFICIAL")) {
            leaderboards.recalculateAll();
            originalGlobalScore = globalScore();
        }
    }

    private AnswerOption option(String text, boolean correct) {
        AnswerOption option = new AnswerOption();
        option.setQuestion(question);
        option.setOptionText(text);
        option.setIsCorrect(correct);
        return options.save(option);
    }

    private Long attempt(AnswerOption selected, boolean correct, int number) {
        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setUser(users.findByUsername("demo").orElseThrow());
        attempt.setStatus(TestAttempt.Status.SUBMITTED);
        attempt.setStartedAt(started);
        attempt.setSubmittedAt(submitted.plusSeconds(number));
        attempt.setDurationSeconds(300);
        attempt.setAttemptNumber(number);
        attempt.setScore(correct ? 2 : 0);
        attempt.setCorrectCount(correct ? 1 : 0);
        attempt.setIncorrectCount(selected != null && !correct ? 1 : 0);
        attempt.setUnansweredCount(selected == null ? 1 : 0);
        attempt.setCountsForLeaderboard(test.getTestType() == Test.TestType.OFFICIAL);
        attempt = attempts.save(attempt);
        savedAttempts.put(attempt.getId(), attempt);
        AnswerAttempt answer = new AnswerAttempt();
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(selected);
        answer.setIsCorrect(correct);
        answer.setAwardedScore(BigDecimal.valueOf(correct ? 2 : 0));
        answers.save(answer);
        return attempt.getId();
    }

    @Given("the question has a report")
    public void reportQuestion() {
        Report report = new Report();
        report.setTest(test);
        report.setQuestion(question);
        report.setReason("Offensive question");
        reportId = reports.save(report).getId();
    }

    @Then("submitted results after adding a question are correct")
    public void resultsAfterAdding() {
        assertEquals(3, maxScore());
        assertAttempt(correctAttempt, 2, 1, 0, 1, 1);
        assertAttempt(incorrectAttempt, 0, 0, 1, 1, 2);
        assertAttempt(unansweredAttempt, 0, 0, 0, 2, 3);
    }

    @Then("submitted results after changing the score are correct")
    public void resultsAfterEdit() {
        assertEquals(4, maxScore());
        assertAttempt(correctAttempt, 4, 1, 0, 0, 1);
        assertAttempt(incorrectAttempt, 0, 0, 1, 0, 2);
        assertAttempt(unansweredAttempt, 0, 0, 0, 1, 3);
        assertEquals(0, jdbc.queryForObject("SELECT awarded_score FROM answer_attempt WHERE attempt_id = ?", BigDecimal.class, correctAttempt).compareTo(BigDecimal.valueOf(4)));
    }

    @Then("submitted results after deleting the question are correct")
    public void resultsAfterDelete() {
        assertEquals(0, maxScore());
        assertAttempt(correctAttempt, 0, 0, 0, 0, 1);
        assertAttempt(incorrectAttempt, 0, 0, 0, 0, 2);
        assertAttempt(unansweredAttempt, 0, 0, 0, 0, 3);
    }

    private void assertAttempt(Long id, int score, int correct, int incorrect, int unanswered, int number) {
        var row = jdbc.queryForMap("SELECT score, correct_count, incorrect_count, unanswered_count, attempt_number, duration_seconds, started_at, submitted_at FROM test_attempt WHERE id = ?", id);
        assertEquals(score, ((Number) row.get("score")).intValue());
        assertEquals(correct, ((Number) row.get("correct_count")).intValue());
        assertEquals(incorrect, ((Number) row.get("incorrect_count")).intValue());
        assertEquals(unanswered, ((Number) row.get("unanswered_count")).intValue());
        assertEquals(number, ((Number) row.get("attempt_number")).intValue());
        assertEquals(300, ((Number) row.get("duration_seconds")).intValue());
        assertEquals(started, jdbc.queryForObject("SELECT started_at FROM test_attempt WHERE id = ?", java.sql.Timestamp.class, id).toInstant());
        assertEquals(submitted.plusSeconds(number), jdbc.queryForObject("SELECT submitted_at FROM test_attempt WHERE id = ?", java.sql.Timestamp.class, id).toInstant());
    }

    @Then("the question options and answers are removed and its report is retained")
    public void dependencies() {
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM answer_option WHERE question_id = ?", Integer.class, question.getId()));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM answer_attempt WHERE question_id = ?", Integer.class, question.getId()));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM report WHERE id = ? AND question_id IS NULL AND test_id = ?", Integer.class, reportId, test.getId()));
    }

    @Then("the rankings reflect the recalculated {word} results")
    public void rankings(String type) {
        int count = jdbc.queryForObject("SELECT COUNT(*) FROM leaderboard WHERE subject_id = ?", Integer.class, test.getSubject().getId());
        if (type.equals("CUSTOM")) {
            assertEquals(0, count);
            return;
        }
        assertEquals(1, count);
        int expected = jdbc.queryForObject("SELECT MAX(score) FROM test_attempt WHERE test_id = ?", Integer.class, test.getId());
        assertEquals(expected, jdbc.queryForObject("SELECT score FROM leaderboard WHERE subject_id = ?", Integer.class, test.getSubject().getId()));
        assertEquals(originalGlobalScore - 2 + expected, globalScore());
    }
    @Given("another question makes the second attempt worth 1 point")
    public void secondQuestionForBestAttempt() {
        Question second = new Question();
        second.setTest(test);
        second.setQuestionText("Second question");
        second.setOrderIndex(1);
        second.setScore(1);
        second = questions.save(second);
        AnswerOption correct = new AnswerOption();
        correct.setQuestion(second);
        correct.setOptionText("Correct second option");
        correct.setIsCorrect(true);
        correct = options.save(correct);
        for (Long id : List.of(correctAttempt, incorrectAttempt, unansweredAttempt)) {
            TestAttempt existing = savedAttempts.get(id);
            AnswerAttempt answer = new AnswerAttempt();
            answer.setAttempt(existing);
            answer.setQuestion(second);
            boolean selected = id.equals(incorrectAttempt);
            answer.setSelectedOption(selected ? correct : null);
            answer.setIsCorrect(selected);
            answer.setAwardedScore(BigDecimal.valueOf(selected ? 1 : 0));
            answers.save(answer);
        }
        jdbc.update("UPDATE test_attempt SET unanswered_count = unanswered_count + 1 WHERE id IN (?, ?)", correctAttempt, unansweredAttempt);
        jdbc.update("UPDATE test_attempt SET score = 1, correct_count = 1 WHERE id = ?", incorrectAttempt);
        tests.updateMaxScore(test.getId(), 3);
        leaderboards.recalculateAll();
    }

    @Then("the second attempt becomes the best result in both rankings")
    public void secondAttemptIsBest() {
        assertEquals(0, jdbc.queryForObject("SELECT score FROM test_attempt WHERE id = ?", Integer.class, correctAttempt));
        assertEquals(1, jdbc.queryForObject("SELECT score FROM test_attempt WHERE id = ?", Integer.class, incorrectAttempt));
        assertEquals(1, jdbc.queryForObject("SELECT score FROM leaderboard WHERE subject_id = ? AND user_id = ?", Integer.class, test.getSubject().getId(), users.findByUsername("demo").orElseThrow().getId()));
        assertEquals(originalGlobalScore - 1, globalScore());
    }

    @Then("the new question follows the highest remaining position")
    public void appendAfterDeletion() {
        Question created = questions.listByTestId(test.getId()).stream()
                .filter(q -> q.getQuestionText().equals("New question")).findFirst().orElseThrow();
        assertEquals(3, created.getOrderIndex());
    }

    private int globalScore() {
        return jdbc.queryForObject("SELECT score FROM leaderboard WHERE leaderboard_type = 'GLOBAL' AND user_id = ?", Integer.class, users.findByUsername("demo").orElseThrow().getId());
    }

    @Given("a published CUSTOM question test with an in-progress attempt")
    public void inProgressAttempt() {
        draftTest("CUSTOM", "demo", 1);
        test.setStatus(Test.Status.PUBLISHED);
        test.setIsPublic(true);
        tests.save(test);
        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setUser(users.findByUsername("demo").orElseThrow());
        attempt.setStatus(TestAttempt.Status.IN_PROGRESS);
        attempt.setStartedAt(started);
        correctAttempt = attempts.save(attempt).getId();
    }

    @When("I submit the in-progress question attempt")
    public void submitQuestionAttempt() throws Exception {
        perform(patch("/testAttempts/" + correctAttempt), new JSONObject().put("status", "SUBMITTED"));
    }

    @Then("the submitted question attempt has {int} unanswered questions")
    public void submittedUnanswered(int count) {
        assertEquals(count, jdbc.queryForObject("SELECT unanswered_count FROM test_attempt WHERE id = ?", Integer.class, correctAttempt));
        assertEquals(0, jdbc.queryForObject("SELECT score FROM test_attempt WHERE id = ?", Integer.class, correctAttempt));
    }

}
