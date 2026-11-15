package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.UriTemplate;
import tools.jackson.databind.JsonNode;

public class HomePageStepDefs {
    private final StepDefs steps;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final TestAttemptRepository attempts;
    private final UserRepository users;
    private final LeaderboardRepository leaderboards;

    private final Map<String, Subject> scenarioSubjects = new HashMap<>();
    private final Map<String, Test> scenarioTests = new HashMap<>();
    private final List<Long> attemptIds = new ArrayList<>();
    private final List<Long> userIds = new ArrayList<>();
    private final List<Long> leaderboardIds = new ArrayList<>();

    public HomePageStepDefs(StepDefs steps, SubjectRepository subjects, TestRepository tests,
                            TestAttemptRepository attempts, UserRepository users,
                            LeaderboardRepository leaderboards) {
        this.steps = steps;
        this.subjects = subjects;
        this.tests = tests;
        this.attempts = attempts;
        this.users = users;
        this.leaderboards = leaderboards;
    }

    @After("@US-COM-02")
    public void removeHomeFixtures() {
        leaderboards.deleteAllById(leaderboardIds);
        attempts.deleteAllById(attemptIds);
        tests.deleteAll(scenarioTests.values());
        users.deleteAllById(userIds);
        subjects.deleteAll(scenarioSubjects.values());
    }

    @Given("these tests exist for the home feed")
    public void seedTests(DataTable table) {
        for (Map<String, String> row : table.asMaps()) {
            Test test = new Test();
            test.setTitle(row.get("title"));
            test.setDescription("Home test description");
            test.setSubject(scenarioSubjects.computeIfAbsent(row.get("subject"), this::createSubject));
            test.setTestType(Test.TestType.valueOf(row.get("type")));
            test.setStatus(Test.Status.valueOf(row.get("status")));
            test.setIsPublic(Boolean.parseBoolean(row.get("public")));
            scenarioTests.put(test.getTitle(), tests.save(test));
        }
    }

    @Given("the same student has these attempts for the home feed")
    public void seedAttempts(DataTable table) {
        User student = createUser("home-test-taker", User.Role.STUDENT);
        Instant now = Instant.now();
        for (Map<String, String> row : table.asMaps()) {
            int count = Integer.parseInt(row.get("count"));
            for (int number = 1; number <= count; number++) {
                TestAttempt attempt = new TestAttempt();
                attempt.setUser(student);
                attempt.setTest(scenarioTests.get(row.get("title")));
                attempt.setAttemptType(TestAttempt.AttemptType.valueOf(row.get("type")));
                attempt.setStatus(TestAttempt.Status.valueOf(row.get("status")));
                attempt.setAttemptNumber(number);
                attempt.setStartedAt(now.minus(Integer.parseInt(row.get("days since start")), ChronoUnit.DAYS));
                if (row.get("days since submission") != null) {
                    attempt.setSubmittedAt(now.minus(
                            Integer.parseInt(row.get("days since submission")), ChronoUnit.DAYS));
                }
                attemptIds.add(attempts.save(attempt).getId());
            }
        }
    }

    @Given("these saved rankings exist for the home feed")
    public void seedRankings(DataTable table) {
        for (Map<String, String> row : table.asMaps()) {
            Leaderboard entry = new Leaderboard();
            entry.setUser(createUser(row.get("username"), User.Role.valueOf(row.get("role"))));
            entry.setLeaderboardType(Leaderboard.LeaderboardType.valueOf(row.get("scope")));
            if (entry.getLeaderboardType() == Leaderboard.LeaderboardType.SUBJECT) {
                entry.setSubject(scenarioSubjects.computeIfAbsent("Ranking subject", this::createSubject));
            }
            entry.setRank(Integer.parseInt(row.get("rank")));
            entry.setScore(Integer.parseInt(row.get("score")));
            entry.setCalculatedAt(Instant.now());
            leaderboardIds.add(leaderboards.save(entry).getId());
        }
    }

    @When("I request the home resource {string}")
    public void requestHomeResource(String path) throws Exception {
        steps.result = steps.mockMvc.perform(get(path)
                .accept(MediaTypes.HAL_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Given("the home requester is {string}")
    public void setRequester(String role) {
        if (role.equals("admin")) {
            AuthenticationStepDefs.currentUsername = "admin";
            AuthenticationStepDefs.currentPassword = "password";
        } else {
            AuthenticationStepDefs.currentUsername = null;
            AuthenticationStepDefs.currentPassword = null;
        }
    }

    @When("I request the home test {string}")
    public void requestHomeTest(String title) throws Exception {
        requestHomeResource(scenarioTests.get(title).getUri());
    }

    @Then("the tests collection includes {string} with its standard fields")
    public void checkStandardTestFields(String title) throws Exception {
        JsonNode entries = responseBody().path("_embedded").path("tests");
        JsonNode entry = null;
        for (JsonNode candidate : entries) {
            if (title.equals(candidate.path("title").asText())) {
                entry = candidate;
                break;
            }
        }
        assertNotNull(entry, "Test missing from collection: " + title);
        assertEquals("Home test description", entry.path("description").asText());
        assertEquals("PUBLISHED", entry.path("status").asText());
        assertTrue(entry.has("createdAt"));
    }

    @Then("the leaderboard collection includes its standard fields")
    public void checkStandardLeaderboardFields() throws Exception {
        JsonNode entries = responseBody().path("_embedded").path("leaderboards");
        assertEquals(1, entries.size());
        assertEquals("GLOBAL", entries.get(0).path("leaderboardType").asText());
        assertTrue(entries.get(0).hasNonNull("calculatedAt"));
    }

    @Then("the leaderboard projection does not expose an administrator username")
    public void checkAdministratorUsernameIsHidden() throws Exception {
        JsonNode entry = responseBody().path("_embedded").path("leaderboards").get(0);
        assertNotNull(entry);
        assertTrue(entry.path("username").isMissingNode() || entry.path("username").isNull());
    }

    @Then("the home response links to {string} at {string}")
    public void checkHomeLink(String relation, String target) throws Exception {
        String href = responseBody().path("_links").path(relation).path("href").asText();
        URI actual = UriTemplate.of(href).expand(Map.of());
        URI expected = URI.create(target);
        assertEquals(expected.getPath(), actual.getPath(), relation + " link path");
        assertEquals(expected.getQuery(), actual.getQuery(), relation + " link query");
    }

    @Then("the home collection {string} is empty")
    public void checkEmptyCollection(String relation) throws Exception {
        JsonNode collection = responseBody().path("_embedded").path(relation);
        assertTrue(collection.isMissingNode() || (collection.isArray() && collection.isEmpty()),
                () -> "Expected an empty HAL collection for " + relation + " but got " + collection);
    }

    @Then("the featured feed contains these tests in order")
    public void checkFeaturedTests(DataTable table) throws Exception {
        List<Map<String, String>> rows = table.asMaps();
        steps.result.andExpect(jsonPath("$._embedded.tests", hasSize(rows.size())));
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            String path = "$._embedded.tests[" + index + "]";
            steps.result.andExpect(jsonPath(path + ".title").value(row.get("title")))
                    .andExpect(jsonPath(path + ".subject.name").value(row.get("subject")))
                    .andExpect(jsonPath(path + ".testType").value(row.get("type")));
        }
    }

    @Then("the global summary contains these rankings in order")
    public void checkGlobalSummary(DataTable table) throws Exception {
        List<Map<String, String>> rows = table.asMaps();
        steps.result.andExpect(jsonPath("$._embedded.leaderboards", hasSize(rows.size())));
        for (int index = 0; index < rows.size(); index++) {
            Map<String, String> row = rows.get(index);
            String path = "$._embedded.leaderboards[" + index + "]";
            steps.result.andExpect(jsonPath(path + ".username").value(row.get("username")))
                    .andExpect(jsonPath(path + ".rank").value(Integer.parseInt(row.get("rank"))))
                    .andExpect(jsonPath(path + ".score").value(Integer.parseInt(row.get("score"))));
        }
    }

    private JsonNode responseBody() throws Exception {
        return steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
    }

    private Subject createSubject(String name) {
        Subject subject = new Subject();
        subject.setName(name);
        return subjects.save(subject);
    }

    private User createUser(String username, User.Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.org");
        user.setRole(role);
        user.setPassword("password");
        user.encodePassword();
        User savedUser = users.save(user);
        userIds.add(savedUser.getId());
        return savedUser;
    }
}
