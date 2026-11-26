package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.*;
import cat.udl.eps.softarch.demo.repository.*;
import cat.udl.eps.softarch.demo.service.LeaderboardService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

public class GlobalLeaderboardStepDefs {
    private final StepDefs steps;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final TestAttemptRepository attempts;
    private final UserRepository users;
    private final LeaderboardRepository entries;
    private final LeaderboardService rankings;
    private final LeaderboardPeriodRepository periods;
    private final List<User> createdUsers = new ArrayList<>();
    private final List<Test> createdTests = new ArrayList<>();
    private final List<TestAttempt> createdAttempts = new ArrayList<>();
    private Subject subject;
    private LeaderboardPeriod closedPeriod;

    public GlobalLeaderboardStepDefs(StepDefs steps, SubjectRepository subjects, TestRepository tests,
            TestAttemptRepository attempts, UserRepository users, LeaderboardRepository entries, LeaderboardService rankings,
            LeaderboardPeriodRepository periods) {
        this.steps = steps;
        this.subjects = subjects;
        this.tests = tests;
        this.attempts = attempts;
        this.users = users;
        this.entries = entries;
        this.rankings = rankings;
        this.periods = periods;
    }

    @Given("global leaderboard results exist")
    public void setup() {
        subject = new Subject();
        subject.setName("Global leaderboard subject");
        subject = subjects.save(subject);
        User a = student("global-a");
        User b = student("global-b");
        User c = student("global-c");
        Test official = test("Global official", Test.TestType.OFFICIAL, Test.Status.PUBLISHED);
        result(official, a, 100);
        result(official, b, 80);
        result(official, c, 80);
        result(official, users.findByUsername("admin").orElseThrow(), 999);
        result(test("Global custom", Test.TestType.CUSTOM, Test.Status.PUBLISHED), b, 500);
        result(test("Global withdrawn", Test.TestType.OFFICIAL, Test.Status.WITHDRAWN), c, 500);
        rankings.recalculateAll();
    }

    private User student(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.org");
        user.setPassword("password123");
        user.encodePassword();
        user = users.save(user);
        createdUsers.add(user);
        return user;
    }

    private Test test(String title, Test.TestType type, Test.Status status) {
        Test test = new Test();
        test.setSubject(subject);
        test.setTitle(title);
        test.setTestType(type);
        test.setStatus(status);
        test.setIsPublic(true);
        test = tests.save(test);
        createdTests.add(test);
        return test;
    }

    private void result(Test test, User user, int score) {
        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setUser(user);
        attempt.setStatus(TestAttempt.Status.SUBMITTED);
        attempt.setScore(score);
        attempt.setAttemptNumber(1);
        attempt.setDurationSeconds(30);
        attempt.setStartedAt(Instant.parse("2025-01-01T00:00:00Z"));
        attempt.setSubmittedAt(Instant.parse("2025-01-01T00:00:30Z"));
        attempt.setCountsForLeaderboard(true);
        createdAttempts.add(attempts.save(attempt));
    }

    @When("I request global leaderboard page {int} of size {int}")
    public void requestPage(int page, int size) throws Exception {
        steps.result = steps.mockMvc.perform(get("/leaderboards/search/findByLeaderboardTypeOrderByRankAsc")
                .param("type", "GLOBAL").param("projection", "leaderboardSummary")
                .param("page", Integer.toString(page)).param("size", Integer.toString(size))
                .accept(MediaType.APPLICATION_JSON).with(AuthenticationStepDefs.authenticate()));
    }

    private JsonNode body() throws Exception {
        return steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
    }

    @Then("the global leaderboard contains")
    public void contains(DataTable table) throws Exception {
        JsonNode items = body().path("_embedded").path("leaderboards");
        List<Map<String, String>> expected = table.asMaps();
        assertEquals(expected.size(), items.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).get("username"), items.get(i).path("username").asString());
            assertEquals(Integer.parseInt(expected.get(i).get("rank")), items.get(i).path("rank").asInt());
            assertEquals(Integer.parseInt(expected.get(i).get("score")), items.get(i).path("score").asInt());
        }
    }

    @Then("the global leaderboard total is {int}")
    public void total(int total) throws Exception {
        assertTrue(body().path("page").has("totalElements"), "Missing pagination metadata");
        assertEquals(total, body().path("page").path("totalElements").asInt());
    }

    @Given("a student chooses leaderboard anonymity")
    @Given("a student chooses leaderboard anonymity after the period closes")
    public void chooseAnonymity() {
        User student = users.findByUsername("global-b").orElseThrow();
        student.setLeaderboardAnonymous(true);
        createdUsers.set(1, users.save(student));
    }

    @When("the student enables leaderboard anonymity in their profile")
    public void enableAnonymity() throws Exception {
        User student = createdUsers.get(1);
        steps.result = steps.mockMvc.perform(patch(student.getUri())
                .with(httpBasic(student.getUsername(), "password123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"leaderboardAnonymous\":true}"));
        createdUsers.set(1, users.findByUsername(student.getUsername()).orElseThrow());
        assertEquals(204, steps.result.andReturn().getResponse().getStatus());
    }

    @Given("a closed period snapshot exists")
    public void closedPeriod() {
        closedPeriod = new LeaderboardPeriod();
        closedPeriod.setStartsAt(Instant.parse("2024-01-01T00:00:00Z"));
        closedPeriod.setEndsAt(Instant.parse("2025-01-01T00:00:00Z"));
        closedPeriod = periods.save(closedPeriod);
        for (Leaderboard current : entries.listAllForRecalculation()) {
            if (current.getLeaderboardType() != Leaderboard.LeaderboardType.GLOBAL ||
                    createdUsers.stream().noneMatch(user -> user.getId().equals(current.getUser().getId()))) continue;
            Leaderboard snapshot = new Leaderboard();
            snapshot.setUser(current.getUser());
            snapshot.setLeaderboardType(Leaderboard.LeaderboardType.GLOBAL);
            snapshot.setPeriod(closedPeriod);
            snapshot.setRank(current.getRank());
            snapshot.setScore(current.getScore());
            entries.save(snapshot);
        }
    }

    @When("I select the closed period after withdrawing an official test")
    public void selectClosedPeriod() throws Exception {
        Test official = createdTests.get(0);
        official.setStatus(Test.Status.WITHDRAWN);
        createdTests.set(0, tests.save(official));
        rankings.recalculateAll();
        steps.result = steps.mockMvc.perform(get("/leaderboardPeriods").accept(MediaType.APPLICATION_JSON));
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        assertTrue(body().path("_embedded").path("leaderboardPeriods").toString().contains(closedPeriod.getUri()));
        steps.result = steps.mockMvc.perform(get("/leaderboards/search/findByLeaderboardTypeOrderByRankAsc")
                .param("type", "GLOBAL").param("period", closedPeriod.getId().toString())
                .param("projection", "leaderboardSummary").param("page", "0").param("size", "10")
                .accept(MediaType.APPLICATION_JSON).with(AuthenticationStepDefs.authenticate()));
    }

    @Then("the original positions and scores are shown")
    public void originalPositions() throws Exception {
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        JsonNode items = body().path("_embedded").path("leaderboards");
        assertEquals(3, items.size());
        assertEquals(100, items.get(0).path("score").asInt());
        assertEquals(1, items.get(0).path("rank").asInt());
        assertEquals(80, items.get(1).path("score").asInt());
        assertEquals(2, items.get(1).path("rank").asInt());
        assertEquals(80, items.get(2).path("score").asInt());
        assertEquals(2, items.get(2).path("rank").asInt());
    }

    @Then("the student is shown as {string} without an identifying link")
    public void anonymousStudent(String name) throws Exception {
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        JsonNode items = body().path("_embedded").path("leaderboards");
        for (JsonNode item : items) {
            if (name.equals(item.path("username").asString())) {
                assertEquals(2, item.path("rank").asInt());
                assertEquals(80, item.path("score").asInt());
                assertTrue(item.path("_links").isMissingNode() || item.path("_links").isEmpty(), item.toString());
                assertFalse(item.toString().contains("global-b"));
                return;
            }
        }
        fail("Anonymous student missing: " + body());
    }

    @Then("public ranking routes do not disclose the anonymous student")
    public void publicRoutesProtectAnonymity() throws Exception {
        List<String> paths = List.of(
                "/leaderboards",
                "/leaderboardSummary",
                "/leaderboards/search/globalSummary",
                "/leaderboards/search/findByLeaderboardTypeOrderByRankAsc?type=GLOBAL",
                "/leaderboards/search/findBySubjectOrderByRankAsc?subject=" + subject.getUri());
        for (String path : paths) {
            steps.result = steps.mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON));
            assertEquals(200, steps.result.andReturn().getResponse().getStatus(), path);
            assertFalse(body().toString().contains("global-b"), path);
            if (path.contains("findBySubject") || path.contains("findByLeaderboardType")
                    || path.contains("Summary")) {
                anonymousStudent("Anonymous student");
            }
        }
        Leaderboard entry = entries.listAllForRecalculation().stream()
                .filter(value -> value.getLeaderboardType() == Leaderboard.LeaderboardType.GLOBAL
                        && value.getUser().getUsername().equals("global-b"))
                .findFirst().orElseThrow();
        steps.result = steps.mockMvc.perform(get(entry.getUri()).accept(MediaType.APPLICATION_JSON));
        assertEquals(404, steps.result.andReturn().getResponse().getStatus());
        steps.result = steps.mockMvc.perform(get(entry.getUri() + "/user").accept(MediaType.APPLICATION_JSON));
        int associationStatus = steps.result.andReturn().getResponse().getStatus();
        assertTrue(associationStatus == 401 || associationStatus == 404);
    }

    @After("@US-COM-07")
    public void cleanup() {
        if (subject == null) return;
        if (closedPeriod != null) {
            for (Leaderboard entry : entries.findAll()) {
                if (entry.getPeriod() != null && entry.getPeriod().getId().equals(closedPeriod.getId())) entries.delete(entry);
            }
            periods.delete(closedPeriod);
        }
        for (Leaderboard entry : entries.listAllForRecalculation()) {
            if (createdUsers.stream().anyMatch(user -> user.getId().equals(entry.getUser().getId()))) entries.delete(entry);
        }
        attempts.deleteAllById(createdAttempts.stream().map(TestAttempt::getId).toList());
        tests.deleteAll(createdTests);
        users.deleteAll(createdUsers);
        subjects.delete(subject);
        rankings.recalculateAll();
    }
}
