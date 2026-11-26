package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Public detail page of a test (US-COM-05). Tests are created with "these tests exist in the platform". */
public class ViewTestDetailsStepDefs {
    private final StepDefs stepDefs;
    private final TestRepository testRepository;

    public ViewTestDetailsStepDefs(StepDefs stepDefs, TestRepository testRepository) {
        this.stepDefs = stepDefs;
        this.testRepository = testRepository;
    }

    private Test test(String title) {
        return testRepository.findByTitle(title).getFirst();
    }

    @When("I open the detail page of the test {string}")
    public void iOpenTheDetailPageOfTheTest(String title) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/tests/{id}", test(title).getId())
                                .param("projection", "testDetail")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I try to take the test {string}")
    public void iTryToTakeTheTest(String title) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/testAttempts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new JSONObject().put("test", test(title).getUri()).toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    private String bodyText() throws Exception {
        return stepDefs.result.andReturn().getResponse().getContentAsString();
    }

    private static String textOrNull(JsonNode node) {
        return node.isMissingNode() || node.isNull() ? null : node.asString();
    }

    private static String sortedTags(String tags) {
        return tags == null ? null : String.join(", ", Arrays.stream(tags.split(",")).map(String::trim).sorted().toList());
    }

    @Then("I see these details of the test")
    public void iSeeTheseDetailsOfTheTest(DataTable table) throws Exception {
        Map<String, String> expected = table.asMaps().getFirst();
        JsonNode test = stepDefs.mapper.readTree(bodyText());

        List<String> tags = new ArrayList<>();
        test.path("tags").forEach(tag -> tags.add(tag.asString()));

        assertEquals(expected.get("title"), textOrNull(test.path("title")), "title");
        assertEquals(expected.get("subject"), textOrNull(test.path("subject").path("name")), "subject");
        assertEquals(expected.get("year"), textOrNull(test.path("year")), "year");
        assertEquals(expected.get("type"), textOrNull(test.path("testType")), "type");
        assertEquals(expected.get("difficulty"), textOrNull(test.path("difficulty")), "difficulty");
        // Tags are a set, so their order is not guaranteed: compare them sorted
        assertEquals(sortedTags(expected.get("tags")), tags.isEmpty() ? null : String.join(", ", tags.stream().sorted().toList()), "tags");
        assertEquals(expected.get("questions"), textOrNull(test.path("questionCount")), "questions");
        assertEquals(expected.get("author"), textOrNull(test.path("authorName")), "author");
    }

    @Then("The test details do not include questions, answer options or correct answers")
    public void theTestDetailsDoNotIncludeQuestionsOrAnswers() throws Exception {
        String body = bodyText();
        JsonNode test = stepDefs.mapper.readTree(body);
        for (String field : List.of("questions", "questionText", "answerOptions", "options", "correct", "isCorrect")) {
            assertFalse(test.has(field), "The details include the field " + field);
            assertFalse(test.path("_links").has(field), "The details link to " + field);
        }
        // The questions created for the test are titled "Question N of <test title>"
        assertFalse(body.contains("Question 1 of"), "The details include the text of a question");
    }
}
