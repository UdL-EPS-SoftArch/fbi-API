package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;

/** US-EST-19: each question has a score of 0 or more (default 1); 0 means "not scored". */
public class DefineQuestionScoringStepDefs {
    private final StepDefs stepDefs;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    public DefineQuestionScoringStepDefs(StepDefs stepDefs, TestRepository testRepository,
                                         QuestionRepository questionRepository) {
        this.stepDefs = stepDefs;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
    }

    @When("I add a question to test {string} without a score")
    public void iAddAQuestionWithoutAScore(String title) throws Throwable {
        postQuestion(questionBody(findTest(title), "Question without score"));
    }

    @When("I add a question to test {string} with a score of {int}")
    public void iAddAQuestionWithAScoreOf(String title, int score) throws Throwable {
        postQuestion(questionBody(findTest(title), "Question worth " + score).put("score", score));
    }

    @When("I {word} the score of question {int} of test {string} to {int}")
    public void iChangeTheScoreOfQuestion(String method, int position, String title, int score) throws Throwable {
        Test test = findTest(title);
        Question question = findQuestion(test, position);
        String uri = "/questions/" + question.getId();

        MockHttpServletRequestBuilder request;
        JSONObject body;
        if ("PUT".equals(method)) {
            // PUT replaces the whole question, so every required field is sent
            body = questionBody(test, question.getQuestionText())
                    .put("questionType", question.getQuestionType().name())
                    .put("score", score);
            request = put(uri);
        } else if ("PATCH".equals(method)) {
            body = new JSONObject().put("score", score);
            request = patch(uri);
        } else {
            throw new IllegalArgumentException("Unsupported method " + method);
        }
        stepDefs.result = stepDefs.mockMvc.perform(withJson(request, body)).andDo(print());
    }

    @Then("Question {int} of test {string} has a score of {int}")
    public void questionHasAScoreOf(int position, String title, int score) {
        assertEquals(score, findQuestion(findTest(title), position).getScore());
    }

    // The API returns a score of 0, which the client shows as "not scored"
    @Then("Question {int} of test {string} is shown as not scored")
    public void questionIsShownAsNotScored(int position, String title) throws Throwable {
        Question question = findQuestion(findTest(title), position);
        stepDefs.mockMvc.perform(get("/questions/" + question.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(0));
    }

    private void postQuestion(JSONObject body) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(withJson(post("/questions"), body)).andDo(print());
    }

    private JSONObject questionBody(Test test, String text) throws Throwable {
        return new JSONObject()
                .put("test", test.getUri())
                .put("questionText", text);
    }

    private MockHttpServletRequestBuilder withJson(MockHttpServletRequestBuilder request, JSONObject body) {
        return request
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate());
    }

    private Test findTest(String title) {
        return testRepository.findByTitle(title).getFirst();
    }

    /** Position is 1-based, following the order of the questions in the test. */
    private Question findQuestion(Test test, int position) {
        return questionRepository.listByTestId(test.getId()).get(position - 1);
    }
}
