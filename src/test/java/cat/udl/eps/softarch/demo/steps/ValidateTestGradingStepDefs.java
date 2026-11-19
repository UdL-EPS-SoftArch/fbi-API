package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class ValidateTestGradingStepDefs {
    private final StepDefs steps;
    private final TestRepository tests;
    private final QuestionRepository questions;
    private final JdbcTemplate jdbc;
    private long attemptsBefore;
    private long answersBefore;
    private int previewScore;

    public ValidateTestGradingStepDefs(StepDefs steps, TestRepository tests,
                                       QuestionRepository questions, JdbcTemplate jdbc) {
        this.steps = steps;
        this.tests = tests;
        this.questions = questions;
        this.jdbc = jdbc;
    }

    private Test test(String title) {
        return tests.findByTitle(title).getFirst();
    }

    private List<Question> questions(String title) {
        return questions.listByTestId(test(title).getId());
    }

    private List<Long> options(Question question) {
        return jdbc.queryForList("SELECT id FROM answer_option WHERE question_id = ? ORDER BY order_index, id",
                Long.class, question.getId());
    }

    private MockHttpServletResponse getJson(String path) throws Exception {
        MockHttpServletResponse response = steps.mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
        return response;
    }

    @When("I submit a grading preview of {string}")
    public void submitPreview(String title) throws Exception {
        List<Question> list = questions(title);
        JSONArray answers = new JSONArray()
                .put(new JSONObject().put("questionId", list.get(0).getId())
                        .put("selectedOptionId", options(list.get(0)).get(0)))
                .put(new JSONObject().put("questionId", list.get(1).getId())
                        .put("selectedOptionId", options(list.get(1)).get(1)));
        attemptsBefore = jdbc.queryForObject("SELECT COUNT(*) FROM test_attempt", Long.class);
        answersBefore = jdbc.queryForObject("SELECT COUNT(*) FROM answer_attempt", Long.class);
        steps.result = steps.mockMvc.perform(post("/test-previews").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new JSONObject().put("test", test(title).getUri()).put("answers", answers).toString())
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Then("The preview score is {int} and no attempt or answer was saved")
    public void previewScore(int score) throws Exception {
        previewScore = new JSONObject(steps.result.andReturn().getResponse().getContentAsString()).getInt("score");
        assertEquals(score, previewScore);
        assertEquals(attemptsBefore, jdbc.queryForObject("SELECT COUNT(*) FROM test_attempt", Long.class));
        assertEquals(answersBefore, jdbc.queryForObject("SELECT COUNT(*) FROM answer_attempt", Long.class));
    }

    @When("I submit the same answers in a real attempt on {string} as {string}")
    public void realAttempt(String title, String username) throws Exception {
        AuthenticationStepDefs.currentUsername = username;
        AuthenticationStepDefs.currentPassword = "password";
        MockHttpServletResponse created = steps.mockMvc.perform(post("/testAttempts")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(new JSONObject().put("test", test(title).getUri()).toString())
                .with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
        assertEquals(201, created.getStatus(), created.getContentAsString());
        String attempt = created.getHeader("Location");
        List<Question> list = questions(title);
        for (int i = 0; i < 2; i++) {
            JSONObject answer = new JSONObject().put("attempt", attempt)
                    .put("question", list.get(i).getUri())
                    .put("selectedOption", "/answerOptions/" + options(list.get(i)).get(i));
            MockHttpServletResponse response = steps.mockMvc.perform(post("/answerAttempts")
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .content(answer.toString()).with(AuthenticationStepDefs.authenticate())).andReturn().getResponse();
            assertEquals(201, response.getStatus(), response.getContentAsString());
        }
        steps.result = steps.mockMvc.perform(patch(attempt).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content("{\"status\":\"SUBMITTED\"}")
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Then("The real attempt has the preview score")
    public void sameScore() throws Exception {
        MockHttpServletResponse response = steps.result.andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
        assertEquals(previewScore, new JSONObject(response.getContentAsString()).getInt("score"));
    }

    @Then("The answer key for {string} shows scores {int}, {int} and {int} and maximum {int}")
    public void answerKey(String title, int first, int second, int third, int maximum) throws Exception {
        int[] scores = {first, second, third};
        List<Question> list = questions(title);
        int sum = 0;
        assertEquals(scores.length, list.size());
        for (int i = 0; i < scores.length; i++) {
            JSONObject question = new JSONObject(getJson(list.get(i).getUri()).getContentAsString());
            assertEquals("Question worth " + scores[i], question.getString("questionText"));
            assertEquals(scores[i], question.getInt("score"));
            sum += question.getInt("score");
            assertOptions(list.get(i), true);
        }
        JSONObject body = new JSONObject(getJson(test(title).getUri()).getContentAsString());
        assertEquals(maximum, body.getInt("maxScore"));
        assertEquals(sum, body.getInt("maxScore"));
    }

    @Then("The answer key for {string} shows a question without a correct answer")
    public void incompleteKey(String title) throws Exception {
        List<Question> list = questions(title);
        assertEquals(1, list.size());
        JSONObject question = new JSONObject(getJson(list.get(0).getUri()).getContentAsString());
        assertEquals("Question worth 2", question.getString("questionText"));
        assertEquals(2, question.getInt("score"));
        assertOptions(list.get(0), false);
    }

    private void assertOptions(Question question, boolean firstCorrect) throws Exception {
        List<Long> ids = options(question);
        assertEquals(2, ids.size());
        for (int i = 0; i < ids.size(); i++) {
            JSONObject option = new JSONObject(getJson("/answerOptions/" + ids.get(i) + "?projection=answerKey")
                    .getContentAsString());
            assertEquals("Option " + (i + 1), option.getString("optionText"));
            assertEquals(firstCorrect && i == 0, option.getBoolean("isCorrect"));
        }
    }
}
