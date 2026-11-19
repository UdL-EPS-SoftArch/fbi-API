package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.*;
import cat.udl.eps.softarch.demo.repository.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class AutomaticScoresStepDefs {
    private final StepDefs steps;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final QuestionRepository questions;
    private final AnswerOptionRepository options;
    private final JdbcTemplate jdbc;
    private Test test;
    private final List<Question> items = new ArrayList<>();
    private final List<AnswerOption> correct = new ArrayList<>();
    private final List<AnswerOption> incorrect = new ArrayList<>();
    private String student;
    private String author;
    private String attempt;
    private String edit;

    public AutomaticScoresStepDefs(StepDefs steps, UserRepository users, SubjectRepository subjects,
            TestRepository tests, QuestionRepository questions, AnswerOptionRepository options, JdbcTemplate jdbc) {
        this.steps = steps;
        this.users = users;
        this.subjects = subjects;
        this.tests = tests;
        this.questions = questions;
        this.options = options;
        this.jdbc = jdbc;
    }

    @Given("a published {word} scoring test")
    public void scoringTest(String type) {
        student = "scoring-" + UUID.randomUUID();
        User user = new User();
        user.setUsername(student);
        user.setEmail(student + "@example.test");
        user.setPassword("password");
        user.encodePassword();
        users.save(user);
        author = type.equals("OFFICIAL") ? "admin" : "demo";
        Subject subject = new Subject();
        subject.setName("Scoring " + UUID.randomUUID());
        subject = subjects.save(subject);
        test = new Test();
        test.setTitle("Scoring " + UUID.randomUUID());
        test.setSubject(subject);
        test.setAuthor(users.findByUsername(author).orElseThrow());
        test.setTestType(Test.TestType.valueOf(type));
        test.setStatus(Test.Status.PUBLISHED);
        test.setIsPublic(true);
        test = tests.save(test);
        for (int i = 0; i < 3; i++) {
            Question question = new Question();
            question.setTest(test);
            question.setQuestionText("Question " + i);
            question.setOrderIndex(i);
            question.setScore(i == 0 ? 2 : 3);
            question = questions.save(question);
            items.add(question);
            correct.add(option(question, true));
            incorrect.add(option(question, false));
        }
    }

    private AnswerOption option(Question question, boolean isCorrect) {
        AnswerOption option = new AnswerOption();
        option.setQuestion(question);
        option.setOptionText(isCorrect ? "Correct" : "Incorrect");
        option.setIsCorrect(isCorrect);
        return options.save(option);
    }

    private void login(String username) {
        AuthenticationStepDefs.currentUsername = username;
        AuthenticationStepDefs.currentPassword = "password";
    }

    private MockHttpServletResponse request(MockHttpServletRequestBuilder request, JSONObject body) throws Exception {
        return steps.mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(body.toString()).with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
    }

    @Given("a student starts and answers a scoring attempt")
    public void startAttempt() throws Exception {
        login(student);
        MockHttpServletResponse created = request(post("/testAttempts"), new JSONObject().put("test", test.getUri()));
        assertEquals(201, created.getStatus(), created.getContentAsString());
        attempt = created.getHeader("Location");
        for (int i = 0; i < 2; i++) {
            AnswerOption selected = i == 0 ? correct.get(i) : incorrect.get(i);
            JSONObject body = new JSONObject().put("attempt", attempt).put("question", items.get(i).getUri())
                    .put("selectedOption", selected.getUri()).put("isCorrect", i != 0).put("awardedScore", 999);
            MockHttpServletResponse response = request(post("/answerAttempts"), body);
            assertEquals(201, response.getStatus(), response.getContentAsString());
        }
    }

    @When("the student submits the scoring attempt")
    public void submit() throws Exception {
        login(student);
        MockHttpServletResponse response = request(patch(attempt), new JSONObject().put("status", "SUBMITTED").put("score", 999));
        assertEquals(200, response.getStatus(), response.getContentAsString());
    }

    @When("a student answers and submits with forged grading data")
    public void answerAndSubmit() throws Exception {
        startAttempt();
        submit();
    }

    @When("a student answers and submits a new scoring attempt")
    public void newAttempt() throws Exception {
        answerAndSubmit();
    }

    @When("the scoring author changes the first question points")
    public void changePoints() throws Exception {
        login(author);
        MockHttpServletResponse response = request(patch(items.get(0).getUri()), new JSONObject().put("score", 7));
        assertEquals(200, response.getStatus(), response.getContentAsString());
        edit = "points";
    }

    @When("the scoring author changes the second correct answer")
    public void changeKey() throws Exception {
        login(author);
        MockHttpServletResponse response = request(patch(incorrect.get(1).getUri()), new JSONObject().put("isCorrect", true));
        assertEquals(200, response.getStatus(), response.getContentAsString());
        edit = "key";
    }

    @When("the scoring author deletes the answered {word}")
    public void deleteAnsweredContent(String content) throws Exception {
        login(author);
        String uri = content.equals("question") ? items.get(0).getUri() : correct.get(0).getUri();
        MockHttpServletResponse response = request(delete(uri), new JSONObject());
        assertTrue(response.getStatus() == 200 || response.getStatus() == 204, response.getContentAsString());
    }

    @When("the scoring test is changed before submission")
    public void changeVersion() throws Exception {
        changePoints();
        scoringEditPersisted();
        changeKey();
        scoringEditPersisted();
        login(author);
        MockHttpServletResponse response = request(post("/questions"), new JSONObject().put("test", test.getUri()).put("questionText", "Added"));
        assertEquals(201, response.getStatus(), response.getContentAsString());
    }

    @Then("the scoring edit is persisted")
    public void scoringEditPersisted() {
        if (edit.equals("points")) {
            assertEquals(7, jdbc.queryForObject("SELECT score FROM question WHERE id = ?", Integer.class, items.get(0).getId()));
        } else {
            assertEquals(Boolean.TRUE, jdbc.queryForObject("SELECT is_correct FROM answer_option WHERE id = ?", Boolean.class, incorrect.get(1).getId()));
            assertEquals(Boolean.FALSE, jdbc.queryForObject("SELECT is_correct FROM answer_option WHERE id = ?", Boolean.class, correct.get(1).getId()));
        }
    }

    @When("{string} tries to change the recorded score")
    public void changeRecordedScore(String actor) throws Exception {
        login(actor.equals("student") ? student : actor);
        steps.result = steps.mockMvc.perform(patch(attempt).contentType(MediaType.APPLICATION_JSON)
                .content("{\"score\":999}").with(AuthenticationStepDefs.authenticate()));
    }

    @Then("the score change is rejected")
    public void rejected() {
        int status = steps.result.andReturn().getResponse().getStatus();
        assertTrue(status == 403 || status == 404 || status == 409, "Unexpected status " + status);
    }

    @Then("the persisted attempt score remains {int}")
    public void persistedScore(int score) throws Exception {
        login(student);
        MockHttpServletResponse response = steps.mockMvc.perform(get(attempt).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
        assertEquals(score, new JSONObject(response.getContentAsString()).getInt("score"));
    }

    @Then("the persisted attempt score is {int} with server-graded answers")
    public void gradedAnswers(int score) throws Exception {
        persistedScore(score);
        long id = Long.parseLong(attempt.substring(attempt.lastIndexOf('/') + 1));
        assertEquals(score == 10 ? 4 : 3, jdbc.queryForObject("SELECT COUNT(*) FROM answer_attempt WHERE attempt_id = ?", Integer.class, id));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM answer_attempt WHERE attempt_id = ? AND awarded_score = 999", Integer.class, id));
        if (score == 2) {
            assertEquals(Boolean.TRUE, jdbc.queryForObject("SELECT is_correct FROM answer_attempt WHERE attempt_id = ? AND question_id = ?", Boolean.class, id, items.get(0).getId()));
            assertEquals(Boolean.FALSE, jdbc.queryForObject("SELECT is_correct FROM answer_attempt WHERE attempt_id = ? AND question_id = ?", Boolean.class, id, items.get(1).getId()));
        }
    }
}
