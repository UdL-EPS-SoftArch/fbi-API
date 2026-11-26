package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ReorderQuestionsStepDefs {
    private final StepDefs steps;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final QuestionRepository questions;
    private final TestAttemptRepository attempts;
    private Test test;
    private final List<Long> ids = new ArrayList<>();
    private Long attemptId;

    public ReorderQuestionsStepDefs(StepDefs steps, UserRepository users, SubjectRepository subjects,
            TestRepository tests, QuestionRepository questions, TestAttemptRepository attempts) {
        this.steps = steps;
        this.users = users;
        this.subjects = subjects;
        this.tests = tests;
        this.questions = questions;
        this.attempts = attempts;
    }

    @Given("a public test with three ordered questions for reordering")
    public void createTest() {
        Subject subject = new Subject();
        subject.setName("Reorder " + UUID.randomUUID());
        subject = subjects.save(subject);
        test = new Test();
        test.setSubject(subject);
        test.setAuthor(users.findByUsername("demo").orElseThrow());
        test.setTitle("Reorder " + UUID.randomUUID());
        test.setStatus(Test.Status.PUBLISHED);
        test.setIsPublic(true);
        test = tests.save(test);
        for (int i = 0; i < 3; i++) {
            Question question = new Question();
            question.setTest(test);
            question.setQuestionText("Question " + i);
            question.setOrderIndex(i);
            ids.add(questions.save(question).getId());
        }
    }

    private List<Long> order(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt)
                .map(i -> i == 999 ? Long.MAX_VALUE : ids.get(i)).toList();
    }

    @When("the author saves the question order {string}")
    public void reorder(String value) throws Exception {
        JSONObject body = new JSONObject().put("test", test.getUri()).put("questionIds", new JSONArray(order(value)));
        steps.result = steps.mockMvc.perform(post("/question-reorders").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(body.toString()).with(AuthenticationStepDefs.authenticate()));
    }

    @When("I change a question position directly")
    public void directUpdate() throws Exception {
        steps.result = steps.mockMvc.perform(patch("/questions/" + ids.getFirst())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(new JSONObject().put("orderIndex", 2).toString())
                .with(AuthenticationStepDefs.authenticate()));
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
    }

    @Then("the public question order is {string}")
    public void publicOrder(String value) throws Exception {
        if (AuthenticationStepDefs.currentUsername == null) {
            AuthenticationStepDefs.currentUsername = "demo";
            AuthenticationStepDefs.currentPassword = "password";
        }
        var response = steps.mockMvc.perform(get("/questions/search/findByTest")
                .param("test", test.getUri()).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
        JSONArray found = new JSONObject(response.getContentAsString()).getJSONObject("_embedded").getJSONArray("questions");
        List<Long> expected = order(value);
        assertEquals(expected.size(), found.length());
        for (int i = 0; i < found.length(); i++) {
            assertTrue(found.getJSONObject(i).getJSONObject("_links").getJSONObject("self").getString("href")
                    .endsWith("/" + expected.get(i)));
        }
    }

    @Then("the reorder request is denied")
    public void denied() {
        int status = steps.result.andReturn().getResponse().getStatus();
        assertTrue(status == 401 || status == 403 || status == 404, "Unexpected status: " + status);
    }

    private Long createAttempt() throws Exception {
        var response = steps.mockMvc.perform(post("/testAttempts").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(new JSONObject().put("test", test.getUri()).toString())
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(201, response.getStatus(), response.getContentAsString());
        String location = response.getHeader("Location");
        return Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
    }

    @Given("an attempt exists before reordering")
    public void existingAttempt() throws Exception {
        AuthenticationStepDefs.currentUsername = "demo";
        AuthenticationStepDefs.currentPassword = "password";
        attemptId = createAttempt();
    }

    private void assertAttemptOrder(Long id, String value) throws Exception {
        var response = steps.mockMvc.perform(get("/testAttempts/" + id).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
        JSONArray actual = new JSONObject(response.getContentAsString()).getJSONArray("questionIds");
        List<Long> actualIds = new ArrayList<>();
        for (int i = 0; i < actual.length(); i++) actualIds.add(actual.getLong(i));
        assertEquals(order(value), actualIds);
    }

    @Given("the existing attempt is submitted")
    public void submitAttempt() throws Exception {
        var response = steps.mockMvc.perform(patch("/testAttempts/" + attemptId)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(new JSONObject().put("status", "SUBMITTED").toString())
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
    }

    @Then("the existing attempt retains order {string}")
    public void existingOrder(String value) throws Exception { assertAttemptOrder(attemptId, value); }

    @Then("a new attempt uses order {string}")
    public void newOrder(String value) throws Exception { assertAttemptOrder(createAttempt(), value); }

    @Given("an old attempt without a recorded order")
    public void oldAttempt() {
        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setUser(users.findByUsername("demo").orElseThrow());
        attemptId = attempts.save(attempt).getId();
        AuthenticationStepDefs.currentUsername = "demo";
        AuthenticationStepDefs.currentPassword = "password";
    }

    @Then("the old attempt question order is null")
    public void oldOrder() throws Exception {
        var response = steps.mockMvc.perform(get("/testAttempts/" + attemptId).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(200, response.getStatus());
        JSONObject body = new JSONObject(response.getContentAsString());
        assertTrue(body.has("questionIds"));
        assertTrue(body.isNull("questionIds"));
    }
}
