package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SearchTestsStepDefs {
    private final StepDefs stepDefs;
    private final SubjectRepository subjectRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final UserRepository userRepository;

    private final Map<String, Subject> subjects = new HashMap<>();

    public SearchTestsStepDefs(StepDefs stepDefs, SubjectRepository subjectRepository,
                               TestRepository testRepository, TestAttemptRepository testAttemptRepository,
                               UserRepository userRepository) {
        this.stepDefs = stepDefs;
        this.subjectRepository = subjectRepository;
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.userRepository = userRepository;
    }

    private Subject subject(String name) {
        return subjects.computeIfAbsent(name, key -> {
            Subject subject = new Subject();
            subject.setName(key);
            return subjectRepository.save(subject);
        });
    }

    @Given("these catalog tests exist")
    public void theseCatalogTestsExist(DataTable table) {
        User student = userRepository.findByUsername("demo").orElseThrow();
        for (Map<String, String> row : table.asMaps()) {
            Test test = new Test();
            test.setTitle(row.get("title"));
            test.setSubject(subject(row.get("subject")));
            test.setTestType(Test.TestType.valueOf(row.get("type")));
            test.setDifficulty(Test.Difficulty.valueOf(row.get("difficulty")));
            test.setTags(Arrays.asList(row.get("tags").split(",")));
            test.setStatus(Test.Status.valueOf(row.get("status")));
            test.setIsPublic(Boolean.parseBoolean(row.get("public")));
            test = testRepository.save(test);
            saveAttempts(test, student, TestAttempt.Status.SUBMITTED, Integer.parseInt(row.get("completed attempts")));
            saveAttempts(test, student, TestAttempt.Status.IN_PROGRESS, Integer.parseInt(row.get("unfinished attempts")));
        }
    }

    private void saveAttempts(Test test, User student, TestAttempt.Status status, int count) {
        for (int i = 0; i < count; i++) {
            TestAttempt attempt = new TestAttempt();
            attempt.setTest(test);
            attempt.setUser(student);
            attempt.setStatus(status);
            attempt.setStartedAt(Instant.now());
            if (status == TestAttempt.Status.SUBMITTED) {
                attempt.setSubmittedAt(Instant.now());
            }
            testAttemptRepository.save(attempt);
        }
    }

    @When("I search the catalog with {string}")
    public void iSearchTheCatalogWith(String query) throws Exception {
        search("/tests/search/catalog", query);
    }

    @When("I search the catalog by popularity with {string}")
    public void iSearchTheCatalogByPopularityWith(String query) throws Exception {
        search("/tests/search/catalogByPopularity", query);
    }

    // The query uses subject names for readability; the API expects the subject URI
    private void search(String path, String query) throws Exception {
        UriComponentsBuilder uri = UriComponentsBuilder.fromPath(path).queryParam("size", 50);
        if (!query.isBlank()) {
            for (String pair : query.split("&")) {
                String[] parts = pair.split("=", 2);
                String value = parts.length > 1 ? parts[1] : "";
                if (parts[0].equals("subject")) {
                    value = "http://localhost" + subject(value).getUri();
                }
                uri.queryParam(parts[0], value);
            }
        }
        stepDefs.result = stepDefs.mockMvc.perform(
                        get(uri.encode(StandardCharsets.UTF_8).build().toUri())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    private List<String> resultTitles() throws Exception {
        JsonNode body = stepDefs.mapper.readTree(stepDefs.result.andReturn().getResponse().getContentAsString());
        List<String> titles = new ArrayList<>();
        for (JsonNode test : body.path("_embedded").path("tests")) {
            titles.add(test.path("title").asString());
        }
        return titles;
    }

    private static List<String> list(String titles) {
        return titles.isBlank() ? List.of() : Arrays.stream(titles.split(",")).map(String::trim).toList();
    }

    @Then("The catalog results are {string}")
    public void theCatalogResultsAre(String titles) throws Exception {
        Set<String> expected = new TreeSet<>(list(titles));
        assertEquals(expected, new TreeSet<>(resultTitles()));
    }

    @Then("The catalog results in order are {string}")
    public void theCatalogResultsInOrderAre(String titles) throws Exception {
        assertEquals(list(titles), resultTitles());
    }

    @Then("The catalog has no results")
    public void theCatalogHasNoResults() throws Exception {
        assertEquals(List.of(), resultTitles());
        JsonNode body = stepDefs.mapper.readTree(stepDefs.result.andReturn().getResponse().getContentAsString());
        assertEquals(0, body.path("page").path("totalElements").asInt());
    }

    @When("I create a test {string} in subject {string} with difficulty {string} and tags {string}")
    public void iCreateATestWithMetadata(String title, String subjectName, String difficulty, String tags)
            throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("subject", "http://localhost" + subject(subjectName).getUri());
        body.put("difficulty", difficulty);
        body.put("tags", Arrays.asList(tags.split(",", -1)));
        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/tests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stepDefs.mapper.writeValueAsString(body))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @Then("The test {string} has difficulty {string} and tags {string}")
    public void theTestHasDifficultyAndTags(String title, String difficulty, String tags) {
        Test test = testRepository.findByTitle(title).getFirst();
        assertEquals(Test.Difficulty.valueOf(difficulty), test.getDifficulty());
        assertEquals(new TreeSet<>(list(tags)), new TreeSet<>(test.getTags()));
    }
}
