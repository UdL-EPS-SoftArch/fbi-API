package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.stream.StreamSupport;

public class SubjectStepDefs {
    private final StepDefs stepDefs;
    private final SubjectRepository subjectRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;

    // Id of the subject as it was before the edit, to check that it is never changed
    private Long originalSubjectId;

    // Error thrown by the server when trying to change the id, if it did not answer with a response
    private Exception idChangeError;

    public SubjectStepDefs(StepDefs stepDefs, SubjectRepository subjectRepository,
                           LeaderboardRepository leaderboardRepository, TestRepository testRepository,
                           UserRepository userRepository) {
        this.stepDefs = stepDefs;
        this.subjectRepository = subjectRepository;
        this.leaderboardRepository = leaderboardRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
    }

    @Given("There is a subject with name {string}")
    public void thereIsASubjectWithName(String name) {
        if (!subjectRepository.existsByNameIgnoreCase(name)) {
            Subject subject = new Subject();
            subject.setName(name);
            subjectRepository.save(subject);
        }
    }

    @When("I create a new subject with name {string} and description {string}")
    public void iCreateANewSubject(String name, String description) throws Throwable {
        JSONObject subject = new JSONObject();
        subject.put("name", name);
        subject.put("description", description);

        stepDefs.result = stepDefs.mockMvc.perform(
                post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(subject.toString())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print());
    }

    @And("The subject {string} exists and is active")
    public void theSubjectExistsAndIsActive(String name) {
        List<Subject> subjects = subjectRepository.findByName(name);
        assertEquals(1, subjects.size(), "Expected exactly one subject named \"" + name + "\"");
        assertTrue(subjects.get(0).isActive(), "Subject \"" + name + "\" should be active");
    }

    @And("The subject {string} appears in the subjects list")
    public void theSubjectAppearsInTheSubjectsList(String name) throws Throwable {
        stepDefs.mockMvc.perform(
                get("/subjects")
                    .param("size", "1000")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.subjects[*].name", hasItem(name)));
    }

    @And("The subject {string} has an empty leaderboard")
    public void theSubjectHasAnEmptyLeaderboard(String name) {
        Subject subject = subjectRepository.findByName(name).get(0);
        assertTrue(leaderboardRepository.findBySubjectOrderByRankAsc(subject).isEmpty(),
                "Leaderboard of \"" + name + "\" should be empty");
    }

    @And("There is only one subject named {string}")
    public void thereIsOnlyOneSubjectNamed(String name) {
        assertEquals(1, countSubjectsNamed(name), "Expected exactly one subject named \"" + name + "\"");
    }

    @When("I edit the subject {string} to name {string} and description {string}")
    public void iEditTheSubject(String currentName, String name, String description) throws Throwable {
        Subject subject = findSubject(currentName);
        JSONObject changes = new JSONObject();
        changes.put("name", name);
        changes.put("description", description);
        performPatch(subject, changes);
    }

    @When("I try to change the id of the subject {string}")
    public void iTryToChangeTheIdOfTheSubject(String name) throws Throwable {
        Subject subject = findSubject(name);
        originalSubjectId = subject.getId();
        JSONObject changes = new JSONObject();
        changes.put("id", originalSubjectId + 1000);
        changes.put("description", "Trying to change the id");
        try {
            performPatch(subject, changes);
        } catch (Exception e) {
            // MockMvc rethrows the errors that the server does not turn into a response
            idChangeError = e;
        }
    }

    @Then("The change of the id is rejected")
    public void theChangeOfTheIdIsRejected() throws Throwable {
        if (idChangeError != null) {
            return; // The request failed on the server, so nothing was saved
        }
        int status = stepDefs.result.andReturn().getResponse().getStatus();
        assertTrue(status >= 400, "Changing the id should be rejected, but the response code was " + status);
    }

    @And("The subject {string} has description {string}")
    public void theSubjectHasDescription(String name, String description) {
        assertEquals(description, findSubject(name).getDescription());
    }

    @And("There is no subject named {string}")
    public void thereIsNoSubjectNamed(String name) {
        assertEquals(0, countSubjectsNamed(name), "There should be no subject named \"" + name + "\"");
    }

    @Given("The subject {string} has a test {string} and a leaderboard entry of {int} points for {string}")
    public void theSubjectHasATestAndALeaderboardEntry(String name, String title, int score, String username) {
        Subject subject = findSubject(name);
        originalSubjectId = subject.getId();

        Test test = new Test();
        test.setSubject(subject);
        test.setTitle(title);
        testRepository.save(test);

        Leaderboard entry = new Leaderboard();
        entry.setUser(userRepository.findByUsername(username).orElseThrow());
        entry.setSubject(subject);
        entry.setLeaderboardType(Leaderboard.LeaderboardType.SUBJECT);
        entry.setScore(score);
        entry.setRank(1);
        entry.setCalculatedAt(Instant.now());
        leaderboardRepository.save(entry);
    }

    @And("The subject {string} keeps its test {string} and its leaderboard entry of {int} points")
    public void theSubjectKeepsItsTestAndLeaderboardEntry(String name, String title, int score) {
        Subject subject = findSubject(name);
        assertEquals(originalSubjectId, subject.getId(), "The id of the subject must not change");

        // findByTitle is not filtered by visibility, so it can be used without a logged in user
        List<Test> tests = testRepository.findByTitle(title);
        assertEquals(1, tests.size(), "Expected exactly one test titled \"" + title + "\"");
        assertEquals(subject.getId(), tests.get(0).getSubject().getId(), "The test must still belong to the subject");

        List<Leaderboard> entries = leaderboardRepository.findBySubjectOrderByRankAsc(subject);
        assertEquals(1, entries.size(), "The leaderboard of the subject must keep its entry");
        assertEquals(score, entries.get(0).getScore(), "The score of the leaderboard entry must not change");
    }

    @And("The subject {string} keeps its id")
    public void theSubjectKeepsItsId(String name) {
        List<Subject> subjects = subjectRepository.findByName(name);
        assertEquals(1, subjects.size(), "Expected exactly one subject named \"" + name + "\"");
        assertEquals(originalSubjectId, subjects.get(0).getId(), "The id of the subject must not change");
        assertFalse(subjectRepository.existsById(originalSubjectId + 1000), "No subject may appear with the new id");
    }

    @Given("There is an archived subject with name {string}")
    public void thereIsAnArchivedSubjectWithName(String name) {
        thereIsASubjectWithName(name);
        Subject subject = findSubject(name);
        subject.setActive(false);
        subjectRepository.save(subject);
    }

    @Given("The subject {string} has a test {string}")
    public void theSubjectHasATest(String name, String title) {
        Test test = new Test();
        test.setSubject(findSubject(name));
        test.setTitle(title);
        testRepository.save(test);
    }

    @When("I delete the subject {string}")
    public void iDeleteTheSubject(String name) throws Throwable {
        // Without an Accept header Spring Data REST answers 204 No Content (with it, 200 and the deleted body)
        stepDefs.result = stepDefs.mockMvc.perform(
                delete(findSubject(name).getUri())
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print());
    }

    @When("I archive the subject {string}")
    public void iArchiveTheSubject(String name) throws Throwable {
        performPatch(findSubject(name), new JSONObject().put("active", false));
    }

    @When("I restore the subject {string}")
    public void iRestoreTheSubject(String name) throws Throwable {
        performPatch(findSubject(name), new JSONObject().put("active", true));
    }

    @When("I create a test {string} in the subject {string}")
    public void iCreateATestInTheSubject(String title, String subjectName) throws Throwable {
        JSONObject test = new JSONObject();
        test.put("title", title);
        test.put("subject", findSubject(subjectName).getUri());

        stepDefs.result = stepDefs.mockMvc.perform(
                post("/tests")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(test.toString())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print());
    }

    @And("The subject {string} is archived")
    public void theSubjectIsArchived(String name) {
        assertFalse(findSubject(name).isActive(), "Subject \"" + name + "\" should be archived");
    }

    // Public view (as a visitor), like "appears in the subjects list": administrators also see archived subjects
    @And("The subject {string} does not appear in the subjects list")
    public void theSubjectDoesNotAppearInTheSubjectsList(String name) throws Throwable {
        stepDefs.mockMvc.perform(
                get("/subjects")
                    .param("size", "1000")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.subjects[*].name", not(hasItem(name))));
    }

    @And("The subject {string} does not appear when searching for {string}")
    public void theSubjectDoesNotAppearWhenSearchingFor(String name, String text) throws Throwable {
        stepDefs.mockMvc.perform(
                get("/subjects/search/findByNameContainingIgnoreCase")
                    .param("text", text)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.subjects[*].name", not(hasItem(name))));
    }

    @And("The logged in user sees the subject {string} in the subjects list")
    public void theLoggedInUserSeesTheSubjectInTheSubjectsList(String name) throws Throwable {
        stepDefs.mockMvc.perform(
                get("/subjects")
                    .param("size", "1000")
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.subjects[*].name", hasItem(name)));
    }

    private void performPatch(Subject subject, JSONObject changes) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                patch(subject.getUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(changes.toString())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print());
    }

    private Subject findSubject(String name) {
        List<Subject> subjects = subjectRepository.findByName(name);
        assertFalse(subjects.isEmpty(), "Expected a subject named \"" + name + "\", but none was found");
        return subjects.get(0);
    }

    // findAll() without paging is not filtered (archived subjects included), so it works without a logged in user
    private long countSubjectsNamed(String name) {
        return StreamSupport.stream(subjectRepository.findAll().spliterator(), false)
                .filter(s -> s.getName().trim().equalsIgnoreCase(name.trim()))
                .count();
    }
}
