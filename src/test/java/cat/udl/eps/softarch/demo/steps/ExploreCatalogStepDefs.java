package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreCatalogStepDefs {
    private final StepDefs stepDefs;
    private final SubjectRepository subjectRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final Map<String, Subject> subjects = new HashMap<>();

    public ExploreCatalogStepDefs(StepDefs stepDefs, SubjectRepository subjectRepository,
                                  TestRepository testRepository, QuestionRepository questionRepository,
                                  UserRepository userRepository) {
        this.stepDefs = stepDefs;
        this.subjectRepository = subjectRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    private Subject subject(String name) {
        return subjects.computeIfAbsent(name, key -> {
            Subject subject = new Subject();
            subject.setName(key);
            return subjectRepository.save(subject);
        });
    }

    // Every test is authored by "demo", so the author's own drafts can be checked too
    @Given("these tests exist in the platform")
    public void theseTestsExistInThePlatform(DataTable table) {
        for (Map<String, String> row : table.asMaps()) {
            Test test = new Test();
            test.setTitle(row.get("title"));
            test.setSubject(subject(row.get("subject")));
            test.setAuthor(userRepository.findByUsername("demo").orElseThrow());
            test.setTestType(Test.TestType.valueOf(row.get("type")));
            test.setStatus(Test.Status.valueOf(row.get("status")));
            test.setIsPublic(Boolean.parseBoolean(row.get("public")));
            if (row.get("year") != null) {
                test.setYear(Integer.parseInt(row.get("year")));
            }
            // Optional columns (US-COM-05): difficulty and comma-separated tags
            if (row.get("difficulty") != null) {
                test.setDifficulty(Test.Difficulty.valueOf(row.get("difficulty")));
            }
            if (row.get("tags") != null) {
                test.setTags(Arrays.asList(row.get("tags").split(",")));
            }
            test = testRepository.save(test);
            for (int i = 1; i <= Integer.parseInt(row.get("questions")); i++) {
                Question question = new Question();
                question.setTest(test);
                question.setQuestionText("Question " + i + " of " + test.getTitle());
                question.setOrderIndex(i);
                questionRepository.save(question);
            }
        }
    }

    @When("I open the catalog")
    public void iOpenTheCatalog() throws Exception {
        openCatalog("/tests/search/catalog?projection=catalogTest&sort=title,asc");
    }

    @When("I open page {int} of the catalog with {int} tests per page")
    public void iOpenPageOfTheCatalog(int page, int size) throws Exception {
        openCatalog("/tests/search/catalog?projection=catalogTest&sort=title,asc&page=" + page + "&size=" + size);
    }

    private void openCatalog(String uri) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get(URI.create(uri))
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    private JsonNode body() throws Exception {
        return stepDefs.mapper.readTree(stepDefs.result.andReturn().getResponse().getContentAsString());
    }

    private JsonNode listedTests() throws Exception {
        return body().path("_embedded").path("tests");
    }

    @Then("The catalog lists these tests")
    public void theCatalogListsTheseTests(DataTable table) throws Exception {
        List<Map<String, String>> listed = new ArrayList<>();
        for (JsonNode test : listedTests()) {
            Map<String, String> row = new HashMap<>();
            row.put("title", test.path("title").asString());
            row.put("subject", test.path("subject").path("name").asString());
            row.put("type", test.path("testType").asString());
            row.put("questions", Long.toString(test.path("questionCount").asLong()));
            row.put("year", test.path("year").isNull() ? null : Integer.toString(test.path("year").asInt()));
            listed.add(row);
            // Each listed test links to its detail page
            assertFalse(test.path("_links").path("self").path("href").asString("").isBlank(),
                    "Missing link to the detail page of " + row.get("title"));
        }
        assertEquals(table.asMaps(), listed);
    }

    @Then("The catalog has {int} tests in {int} pages")
    public void theCatalogHasTestsInPages(int total, int pages) throws Exception {
        JsonNode page = body().path("page");
        assertEquals(total, page.path("totalElements").asInt());
        assertEquals(pages, page.path("totalPages").asInt());
    }

    @Then("The catalog page lists {string}")
    public void theCatalogPageLists(String titles) throws Exception {
        List<String> listed = new ArrayList<>();
        for (JsonNode test : listedTests()) {
            listed.add(test.path("title").asString());
        }
        assertEquals(Arrays.stream(titles.split(",")).map(String::trim).toList(), listed);
    }

    // Follows the self link of the test, as the frontend does when the user clicks it
    @When("I select the catalog test {string}")
    public void iSelectTheCatalogTest(String title) throws Exception {
        for (JsonNode test : listedTests()) {
            if (title.equals(test.path("title").asString())) {
                openCatalog(test.path("_links").path("self").path("href").asString().replaceAll("\\{.*", ""));
                return;
            }
        }
        fail("Test missing from the catalog: " + title);
    }

    @Then("I see the details of the test {string}")
    public void iSeeTheDetailsOfTheTest(String title) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.title", is(title)));
    }
}
