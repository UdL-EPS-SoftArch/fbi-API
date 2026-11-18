package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class DefineCorrectAnswerStepDefs {
    private final StepDefs stepDefs;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    // Options are read straight from the database: the repository queries filter them by the
    // current user, and the API never returns whether an option is correct
    private final JdbcTemplate jdbc;

    public DefineCorrectAnswerStepDefs(StepDefs stepDefs, TestRepository testRepository,
                                       QuestionRepository questionRepository, JdbcTemplate jdbc) {
        this.stepDefs = stepDefs;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.jdbc = jdbc;
    }

    @When("I mark option {int} of question {int} of test {string} as correct")
    public void iMarkOptionAsCorrect(int option, int question, String title) throws Throwable {
        JSONObject changes = new JSONObject().put("isCorrect", true);

        stepDefs.result = stepDefs.mockMvc.perform(
                patch("/answerOptions/{id}", optionId(title, question, option))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changes.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I add a correct option to question {int} of test {string}")
    public void iAddACorrectOption(int question, String title) throws Throwable {
        Question target = findQuestion(title, question);
        JSONObject option = new JSONObject()
                .put("question", target.getUri())
                .put("optionText", "New correct option")
                .put("isCorrect", true)
                .put("orderIndex", optionIds(target).size());

        stepDefs.result = stepDefs.mockMvc.perform(
                post("/answerOptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(option.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I retrieve option {int} of question {int} of test {string} with the answer key")
    public void iRetrieveOptionWithAnswerKey(int option, int question, String title) throws Throwable {
        retrieveOption(title, question, option, true);
    }

    @When("I retrieve option {int} of question {int} of test {string} without answer key")
    public void iRetrieveOptionWithoutAnswerKey(int option, int question, String title) throws Throwable {
        retrieveOption(title, question, option, false);
    }

    @Then("Only option {int} of question {int} of test {string} is correct")
    public void onlyOptionIsCorrect(int option, int question, String title) {
        Question target = findQuestion(title, question);
        List<Long> ids = optionIds(target);
        // Positions (1-based) of the options marked as correct, so the message matches the step
        List<Integer> correct = jdbc.queryForList(
                        "SELECT id FROM answer_option WHERE question_id = ? AND is_correct = TRUE ORDER BY order_index, id",
                        Long.class, target.getId())
                .stream().map(id -> ids.indexOf(id) + 1).toList();

        assertEquals(List.of(option), correct,
                "Expected only option " + option + " of question " + question + " to be correct, but the correct options were " + correct);
    }

    @Then("The retrieved option is shown as correct")
    public void theRetrievedOptionIsShownAsCorrect() throws Throwable {
        JSONObject body = responseBody();
        assertTrue(body.has("isCorrect") && body.optBoolean("isCorrect"),
                "Expected the author to see isCorrect = true, but the response was " + body);
    }

    @Then("The retrieved option does not reveal whether it is correct")
    public void theRetrievedOptionDoesNotRevealIt() throws Throwable {
        JSONObject body = responseBody();
        assertTrue(!body.has("isCorrect") || body.isNull("isCorrect"),
                "Expected the response to hide isCorrect, but it was " + body);
    }

    private void retrieveOption(String title, int question, int option, boolean answerKey) throws Throwable {
        var request = get("/answerOptions/{id}", optionId(title, question, option))
                .accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate());
        if (answerKey) {
            request = request.queryParam("projection", "answerKey");
        }
        stepDefs.result = stepDefs.mockMvc.perform(request).andDo(print());
    }

    private JSONObject responseBody() throws Throwable {
        return new JSONObject(stepDefs.result.andReturn().getResponse().getContentAsString());
    }

    private Long optionId(String title, int question, int option) {
        return optionIds(findQuestion(title, question)).get(option - 1);
    }

    private List<Long> optionIds(Question question) {
        return jdbc.queryForList("SELECT id FROM answer_option WHERE question_id = ? ORDER BY order_index, id",
                Long.class, question.getId());
    }

    private Question findQuestion(String title, int position) {
        Test test = testRepository.findByTitle(title).getFirst();
        return questionRepository.listByTestId(test.getId()).get(position - 1);
    }
}
