package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tools.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class BrowseSubjectsStepDefs {
    private final StepDefs steps;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final LeaderboardRepository leaderboards;
    private final UserRepository users;
    private final Map<String, Long> leaderboardIds = new HashMap<>();

    public BrowseSubjectsStepDefs(StepDefs steps, SubjectRepository subjects, TestRepository tests,
                                  LeaderboardRepository leaderboards, UserRepository users) {
        this.steps = steps;
        this.subjects = subjects;
        this.tests = tests;
        this.leaderboards = leaderboards;
        this.users = users;
    }

    @Given("the subject catalogue contains public and non-public tests")
    public void catalogue() {
        User author = users.findByUsername("demo").orElseThrow();
        Subject architecture = subject("Architecture", "Software design");
        subject("Empty", "No tests");
        Subject other = subject("Other", "Other subject");
        test(architecture, author, "Official exam", Test.TestType.OFFICIAL, Test.Status.PUBLISHED, true);
        test(architecture, author, "Custom exam", Test.TestType.CUSTOM, Test.Status.PUBLISHED, true);
        test(architecture, author, "Draft", Test.TestType.CUSTOM, Test.Status.DRAFT, true);
        test(architecture, author, "Withdrawn", Test.TestType.CUSTOM, Test.Status.WITHDRAWN, true);
        test(architecture, author, "Private", Test.TestType.CUSTOM, Test.Status.PUBLISHED, false);
        test(other, author, "Other exam", Test.TestType.OFFICIAL, Test.Status.PUBLISHED, true);
        for (Subject subject : new Subject[]{architecture, other}) {
            Leaderboard entry = new Leaderboard();
            entry.setSubject(subject);
            entry.setUser(author);
            entry.setLeaderboardType(Leaderboard.LeaderboardType.SUBJECT);
            entry.setScore(10);
            entry.setRank(1);
            leaderboardIds.put(subject.getName(), leaderboards.save(entry).getId());
        }
    }

    private Subject subject(String name, String description) {
        Subject subject = new Subject();
        subject.setName(name);
        subject.setDescription(description);
        return subjects.save(subject);
    }

    private void test(Subject subject, User author, String title, Test.TestType type, Test.Status status, boolean isPublic) {
        Test test = new Test();
        test.setSubject(subject);
        test.setAuthor(author);
        test.setTitle(title);
        test.setTestType(type);
        test.setStatus(status);
        test.setIsPublic(isPublic);
        tests.save(test);
    }

    @Given("the catalogue requester is {string}")
    public void requester(String role) {
        AuthenticationStepDefs.currentUsername = role.equals("visitor") ? null : role.equals("student") ? "demo" : "admin";
        AuthenticationStepDefs.currentPassword = role.equals("visitor") ? null : "password";
    }

    @When("I request the subjects catalogue")
    public void requestCatalogue() throws Exception { request("/subjects"); }

    private void request(String uri) throws Exception {
        steps.result = steps.mockMvc.perform(get(URI.create(uri)).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    private JsonNode body() throws Exception {
        return steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
    }

    private JsonNode collection(String name) throws Exception {
        JsonNode embedded = body().path("_embedded");
        return embedded.path(name);
    }

    private JsonNode listedSubject(String name) throws Exception {
        for (JsonNode subject : collection("subjects")) {
            if (name.equals(subject.path("name").asString())) return subject;
        }
        fail("Subject missing from catalogue: " + name);
        return null;
    }

    private String link(JsonNode node, String relation) {
        String href = node.path("_links").path(relation).path("href").asString("");
        assertFalse(href.isBlank(), "Missing HAL link: " + relation);
        return href.replaceAll("\\{.*", "");
    }

    @Then("the catalogue contains subject {string} with description {string} and {int} published tests")
    public void catalogueContains(String name, String description, int count) throws Exception {
        JsonNode subject = listedSubject(name);
        assertEquals(description, subject.path("description").asString());
        assertTrue(subject.has("publishedTestCount"), "Missing publishedTestCount");
        assertEquals(count, subject.path("publishedTestCount").asInt());
        link(subject, "self");
    }

    @When("I open subject {string} from the catalogue")
    public void openSubject(String name) throws Exception {
        requestCatalogue();
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        request(link(listedSubject(name), "self"));
    }

    @Then("the subject detail has description {string} and links to published tests and leaderboard")
    public void detail(String description) throws Exception {
        JsonNode subject = body();
        assertEquals("Architecture", subject.path("name").asString());
        assertEquals(description, subject.path("description").asString());
        link(subject, "publishedTests");
        link(subject, "leaderboard");
    }

    @When("I follow the published tests link for subject {string}")
    public void publishedTests(String name) throws Exception { follow(name, "publishedTests"); }

    @When("I follow the leaderboard link for subject {string}")
    public void leaderboard(String name) throws Exception { follow(name, "leaderboard"); }

    private void follow(String name, String relation) throws Exception {
        openSubject(name);
        assertEquals(200, steps.result.andReturn().getResponse().getStatus());
        request(link(body(), relation));
    }

    @Then("the published tests are exactly {string} of type {string} and {string} of type {string}")
    public void exactTests(String first, String firstType, String second, String secondType) throws Exception {
        JsonNode items = collection("tests");
        assertEquals(2, items.size());
        Map<String, String> actual = new HashMap<>();
        for (JsonNode item : items) {
            link(item, "self");
            actual.put(item.path("title").asString(), item.path("testType").asString());
        }
        assertEquals(Map.of(first, firstType, second, secondType), actual);
    }

    @Then("the published tests collection is empty")
    public void empty() throws Exception { assertEquals(0, collection("tests").size()); }

    @Then("the leaderboard contains only the entry for {string}")
    public void leaderboardContains(String name) throws Exception {
        JsonNode entries = collection("leaderboards");
        assertEquals(1, entries.size());
        assertTrue(URI.create(link(entries.get(0), "self")).getPath().endsWith("/" + leaderboardIds.get(name)));
    }

    @Given("a subject named {string} is archived")
    public void archived(String name) {
        Subject archived = subject(name, "Archived subject");
        archived.setActive(false);
        subjects.save(archived);
    }

    @Then("the catalogue does not contain subject {string}")
    public void absent(String name) throws Exception {
        for (JsonNode subject : collection("subjects")) assertNotEquals(name, subject.path("name").asString());
    }
}
