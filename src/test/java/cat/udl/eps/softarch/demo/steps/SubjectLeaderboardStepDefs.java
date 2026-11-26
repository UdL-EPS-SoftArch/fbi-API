package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.*;
import cat.udl.eps.softarch.demo.repository.*;
import cat.udl.eps.softarch.demo.service.LeaderboardService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

public class SubjectLeaderboardStepDefs {
    private final StepDefs steps;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final TestAttemptRepository attempts;
    private final UserRepository users;
    private final LeaderboardRepository entries;
    private final LeaderboardService rankings;
    private final List<Test> fixtures = new ArrayList<>();
    private final List<TestAttempt> results = new ArrayList<>();
    private Subject subject, empty, other;
    private User tie, student;
    private Test official;
    private Question question;
    private final List<AnswerOption> options = new ArrayList<>();
    private final QuestionRepository questions;
    private final AnswerOptionRepository answerOptions;

    public SubjectLeaderboardStepDefs(StepDefs steps, SubjectRepository subjects, TestRepository tests,
            TestAttemptRepository attempts, UserRepository users, LeaderboardRepository entries, LeaderboardService rankings,
            QuestionRepository questions, AnswerOptionRepository answerOptions) {
        this.questions = questions;
        this.answerOptions = answerOptions;
        this.steps = steps;
        this.subjects = subjects;
        this.tests = tests;
        this.attempts = attempts;
        this.users = users;
        this.entries = entries;
        this.rankings = rankings;
    }

    @Given("subject leaderboard results exist")
    public void setup() {
        subject = subject("Leaderboard subject");
        empty = subject("Empty leaderboard subject");
        other = subject("Other leaderboard subject");
        tie = new User();
        tie.setUsername("ztie");
        tie.setEmail("ztie@example.org");
        tie.setPassword("password123");
        tie = users.save(tie);
        User demo = users.findByUsername("demo").orElseThrow();
        student = new User();
        student.setUsername("test");
        student.setEmail("test@sample.app");
        student.setPassword("password");
        student.encodePassword();
        student = users.save(student);
        User admin = users.findByUsername("admin").orElseThrow();
        official = test(subject, "Official", Test.TestType.OFFICIAL, Test.Status.PUBLISHED);
        result(official, demo, 100);
        result(official, student, 80);
        result(official, tie, 80);
        result(official, admin, 999);
        result(test(subject, "Custom", Test.TestType.CUSTOM, Test.Status.PUBLISHED), demo, 500);
        result(test(subject, "Withdrawn", Test.TestType.OFFICIAL, Test.Status.WITHDRAWN), demo, 500);
        result(test(other, "Other", Test.TestType.OFFICIAL, Test.Status.PUBLISHED), student, 500);
        rankings.recalculateAll();
    }

    private Subject subject(String name) {
        Subject value = new Subject();
        value.setName(name);
        return subjects.save(value);
    }

    private Test test(Subject owner, String title, Test.TestType type, Test.Status status) {
        Test value = new Test();
        value.setSubject(owner);
        value.setTitle(title);
        value.setTestType(type);
        value.setStatus(status);
        value.setIsPublic(true);
        value = tests.save(value);
        fixtures.add(value);
        return value;
    }

    private void result(Test test, User user, int score) {
        TestAttempt value = new TestAttempt();
        value.setTest(test);
        value.setUser(user);
        value.setStatus(TestAttempt.Status.SUBMITTED);
        value.setScore(score);
        value.setAttemptNumber(1);
        value.setDurationSeconds(30);
        value.setStartedAt(Instant.parse("2025-01-01T00:00:00Z"));
        value.setSubmittedAt(Instant.parse("2025-01-01T00:00:30Z"));
        value.setCountsForLeaderboard(true);
        results.add(attempts.save(value));
    }

    @Given("student {string} needed two attempts to reach 80 points")
    public void twoAttempts(String username) {
        TestAttempt first = results.stream()
                .filter(attempt -> attempt.getUser().getUsername().equals(username)
                        && attempt.getTest().getSubject().getId().equals(subject.getId()))
                .findFirst().orElseThrow();
        first.setScore(70);
        attempts.save(first);
        TestAttempt second = new TestAttempt();
        second.setTest(first.getTest());
        second.setUser(first.getUser());
        second.setStatus(TestAttempt.Status.SUBMITTED);
        second.setScore(80);
        second.setAttemptNumber(2);
        second.setDurationSeconds(30);
        second.setStartedAt(first.getSubmittedAt());
        second.setSubmittedAt(first.getSubmittedAt().plusSeconds(30));
        second.setCountsForLeaderboard(true);
        results.add(attempts.save(second));
        rankings.recalculateAll();
    }

    @Given("student {string} needed {int} seconds to reach 80 points")
    public void duration(String username, int seconds) {
        TestAttempt attempt = results.stream()
                .filter(result -> result.getUser().getUsername().equals(username)
                        && result.getTest().getSubject().getId().equals(subject.getId()))
                .findFirst().orElseThrow();
        attempt.setDurationSeconds(seconds);
        attempt.setSubmittedAt(attempt.getStartedAt().plusSeconds(seconds));
        attempts.save(attempt);
        rankings.recalculateAll();
    }

    @After("@US-COM-06")
    public void cleanup() {
        for (var entry : entries.listAllForRecalculation()) {
            if (entry.getSubject() != null && (entry.getSubject().getId().equals(subject.getId()) || entry.getSubject().getId().equals(other.getId()))) entries.delete(entry);
            else if (entry.getUser().getId().equals(tie.getId()) || entry.getUser().getId().equals(student.getId())) entries.delete(entry);
        }
        answerOptions.deleteAll(options);
        if (question != null) {
            questions.delete(question);
            // The status was changed through the API: delete the current version, not the stale one
            fixtures.replaceAll(test -> test.getId().equals(official.getId())
                    ? tests.findByTitle(test.getTitle()).stream()
                            .filter(stored -> stored.getId().equals(test.getId())).findFirst().orElseThrow()
                    : test);
        }
        attempts.deleteAllById(results.stream().map(TestAttempt::getId).toList());
        tests.deleteAll(fixtures);
        users.delete(tie);
        users.delete(student);
        subjects.deleteAll(List.of(subject, empty, other));
        rankings.recalculateAll();
    }

    private JsonNode body() throws Exception {
        return steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
    }

    private void getUri(String uri) throws Exception {
        steps.result = steps.mockMvc.perform(get(URI.create(uri)).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    private String link(JsonNode value, String rel) {
        String href = value.path("_links").path(rel).path("href").asString("");
        assertFalse(href.isBlank(), "Missing link " + rel);
        return href.replaceAll("\\{.*", "");
    }

    private void page(Subject value, int page, int size) throws Exception {
        getUri(value.getUri());
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        String uri = UriComponentsBuilder.fromUriString(link(body(), "leaderboard"))
                .queryParam("projection", "leaderboardSummary").queryParam("page", page).queryParam("size", size)
                .build().toUriString();
        getUri(uri);
    }

    // US-COM-11: through the API, so the test handler recalculates the rankings. The fixture gets a
    // valid question first, because republishing runs the publication checks.
    @When("the administrator sets the official subject test to {string}")
    public void setOfficialStatus(String status) throws Exception {
        if (question == null) {
            question = new Question();
            question.setTest(official);
            question.setQuestionText("Eligible question");
            question.setOrderIndex(0);
            question = questions.save(question);
            for (int i = 0; i < 2; i++) {
                AnswerOption option = new AnswerOption();
                option.setQuestion(question);
                option.setOptionText("Option " + i);
                option.setIsCorrect(i == 0);
                option.setOrderIndex(i);
                options.add(answerOptions.save(option));
            }
        }
        steps.result = steps.mockMvc.perform(patch(official.getUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"" + status + "\"}")
                .accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    @When("I request subject leaderboard page {int} of size {int}")
    public void requestPage(int page, int size) throws Exception { page(subject, page, size); }

    @When("I request the empty subject leaderboard")
    public void requestEmpty() throws Exception { page(empty, 0, 10); }

    @Then("the subject leaderboard page contains")
    public void pageContains(DataTable table) throws Exception {
        JsonNode items = body().path("_embedded").path("leaderboards");
        List<Map<String, String>> expected = table.asMaps();
        assertEquals(expected.size(), items.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).get("username"), items.get(i).path("username").asString());
            assertEquals(Integer.parseInt(expected.get(i).get("rank")), items.get(i).path("rank").asInt());
            assertEquals(Integer.parseInt(expected.get(i).get("score")), items.get(i).path("score").asInt());
        }
    }

    @Then("the subject leaderboard total is {int}")
    public void total(int total) throws Exception {
        assertTrue(body().path("page").has("totalElements"), "Missing pagination metadata");
        assertEquals(total, body().path("page").path("totalElements").asInt());
    }

    @Then("the subject leaderboard is empty")
    public void empty() throws Exception { assertEquals(0, body().path("_embedded").path("leaderboards").size()); }

    @When("I request my subject leaderboard position using my identity")
    public void ownPosition() throws Exception {
        getUri("/identity");
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        String identity = link(body(), "self");
        String uri = UriComponentsBuilder.fromUriString("/leaderboards/search/findByUser")
                .queryParam("user", identity).build().encode().toUriString();
        getUri(uri);
    }

    @Then("my subject leaderboard position is {int} with score {int}")
    public void ownPositionIs(int rank, int score) throws Exception {
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        for (JsonNode item : body().path("_embedded").path("leaderboards")) {
            String subjectLink = link(item, "subject");
            getUri(subjectLink);
            if (link(body(), "self").endsWith(subject.getUri())) {
                getUri(link(item, "self"));
                assertEquals(rank, body().path("rank").asInt());
                assertEquals(score, body().path("score").asInt());
                return;
            }
        }
        fail("Subject position missing");
    }
}
