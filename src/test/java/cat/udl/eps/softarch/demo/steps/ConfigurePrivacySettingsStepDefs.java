package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.JsonNode;

public class ConfigurePrivacySettingsStepDefs {
    private final StepDefs steps;
    private final AuthenticationStepDefs authentication;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final LeaderboardRepository leaderboards;
    private final List<Long> leaderboardIds = new ArrayList<>();
    private Subject subject;
    private Leaderboard globalEntry;
    private JsonNode checkedRow;

    public ConfigurePrivacySettingsStepDefs(StepDefs steps, AuthenticationStepDefs authentication,
            UserRepository users, SubjectRepository subjects, LeaderboardRepository leaderboards) {
        this.steps = steps;
        this.authentication = authentication;
        this.users = users;
        this.subjects = subjects;
        this.leaderboards = leaderboards;
    }

    @After("@US-EST-46")
    public void removePrivacyFixtures() {
        leaderboards.deleteAllById(leaderboardIds);
        if (subject != null) {
            subjects.delete(subject);
        }
        users.findByUsername("privacy-student").ifPresent(users::delete);
        users.findByUsername("privacy-new-student").ifPresent(users::delete);
    }

    @Given("the profile of {string} has an avatar at {string}")
    public void setAvatar(String username, String avatar) {
        User user = findUser(username);
        user.setProfilePicture(avatar);
        users.save(user);
    }

    @Given("privacy leaderboard entries exist for {string}")
    public void createRankings(String username) {
        Subject value = new Subject();
        value.setName("Privacy settings subject");
        subject = subjects.save(value);
        User student = findUser(username);
        saveRanking(findUser("demo"), subject, 1, 100);
        saveRanking(student, subject, 2, 80);
        globalEntry = saveRanking(student, null, 2, 180);
    }

    @Given("the privacy leaderboard viewer is {string}")
    public void loginViewer(String viewer) {
        if (viewer.equals("visitor")) {
            authentication.iMNotLoggedIn();
        } else {
            authentication.iLoginAsWithPassword(viewer, "password");
        }
    }

    @When("I register {string} with email {string} and password {string} without privacy preferences")
    public void registerWithoutPreferences(String username, String email, String password) throws Exception {
        String body = steps.mapper.writeValueAsString(Map.of(
                "username", username, "email", email, "password", password, "acceptTerms", true));
        steps.result = steps.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .with(AuthenticationStepDefs.authenticate()));
    }

    @When("I set {string} to {word} for the profile of {string}")
    public void updatePreference(String field, String value, String username) throws Exception {
        steps.result = steps.mockMvc.perform(patch(findUser(username).getUri())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(steps.mapper.writeValueAsString(Map.of(field, Boolean.parseBoolean(value))))
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Then("the saved privacy settings are profile public {word} and leaderboard anonymous {word}")
    public void checkSavedPreferences(String profilePublic, String anonymous) throws Exception {
        steps.result.andExpect(jsonPath("$.profilePublic").value(Boolean.parseBoolean(profilePublic)))
                .andExpect(jsonPath("$.anonymousInLeaderboards").value(Boolean.parseBoolean(anonymous)));
    }

    @Then("the private profile displays only the public name {string}")
    public void checkPrivateProfile(String username) throws Exception {
        steps.result.andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.profilePicture").doesNotExist())
                .andExpect(jsonPath("$.profilePublic").value(false));
        Set<String> allowedFields = Set.of("username", "profilePublic", "uri", "_links");
        for (var field : responseBody().properties()) {
            assertTrue(field.getValue().isNull() || allowedFields.contains(field.getKey()),
                    () -> "Private profile exposes " + field.getKey());
        }
    }

    @When("I request the privacy leaderboard {string}")
    public void requestProjectedLeaderboard(String view) throws Exception {
        request(leaderboardRequest(view).param("projection", "leaderboardSummary"));
    }

    @When("I request the standard privacy leaderboard {string}")
    public void requestStandardLeaderboard(String view) throws Exception {
        request(leaderboardRequest(view));
    }

    @When("I request only the first row of the privacy subject leaderboard")
    public void requestFirstSubjectRow() throws Exception {
        request(leaderboardRequest("subject").param("projection", "leaderboardSummary")
                .param("page", "0").param("size", "1"));
    }

    @When("I request my privacy leaderboard positions using my identity")
    public void requestOwnPositions() throws Exception {
        request(get("/identity"));
        steps.result.andExpect(status().isOk());
        String identity = responseBody().path("_links").path("self").path("href").asString("");
        assertFalse(identity.isBlank(), "The identity resource must link to the signed-in user");
        request(get("/leaderboards/search/findByUser").param("user", identity)
                .param("projection", "leaderboardSummary"));
    }

    @Then("the privacy subject leaderboard page contains only rank 1")
    public void checkFirstSubjectRow() throws Exception {
        steps.result.andExpect(jsonPath("$._embedded.leaderboards.length()").value(1))
                .andExpect(jsonPath("$._embedded.leaderboards[0].rank").value(1));
    }

    @Then("the leaderboard row at rank {int} with score {int} displays {string}")
    public void checkDisplayName(int rank, int score, String name) throws Exception {
        checkedRow = findRow(rank, score);
        assertEquals(name, checkedRow.path("username").asString());
    }

    @Then("that row does not identify the account of {string}")
    public void checkAccountReference(String username) {
        assertAnonymous(checkedRow, findUser(username));
    }

    @Then("the leaderboard row at rank {int} with score {int} does not identify the account of {string}")
    public void checkAccountReference(int rank, int score, String username) throws Exception {
        assertAnonymous(findRow(rank, score), findUser(username));
    }

    private void assertAnonymous(JsonNode row, User user) {
        String serializedRow = row.toString();
        assertFalse(row.hasNonNull("user") || row.hasNonNull("userId") || row.path("_links").hasNonNull("user")
                || serializedRow.contains(user.getUsername()) || serializedRow.contains(user.getUri()),
                () -> "Anonymous leaderboard row exposes the account: " + row);
    }

    private JsonNode findRow(int rank, int score) throws Exception {
        JsonNode response = responseBody();
        JsonNode rows = response.has("rank") ? steps.mapper.createArrayNode().add(response)
                : response.path("_embedded").path("leaderboards");
        JsonNode match = null;
        for (JsonNode row : rows) {
            if (row.path("rank").asInt() == rank && row.path("score").asInt() == score) {
                assertTrue(match == null, "More than one row has the expected rank and score");
                match = row;
            }
        }
        assertNotNull(match, () -> "Missing leaderboard row at rank " + rank + " with score " + score);
        return match;
    }

    private MockHttpServletRequestBuilder leaderboardRequest(String view) {
        return switch (view) {
            case "subject" -> get("/leaderboards/search/findBySubjectOrderByRankAsc")
                    .param("subject", subject.getUri());
            case "global" -> get("/leaderboards/search/findByLeaderboardTypeOrderByRankAsc").param("type", "GLOBAL");
            case "home" -> get("/leaderboardSummary");
            case "collection" -> get("/leaderboards").param("size", "100");
            case "entry" -> get(globalEntry.getUri());
            default -> throw new IllegalArgumentException("Unknown leaderboard view: " + view);
        };
    }

    private void request(MockHttpServletRequestBuilder request) throws Exception {
        steps.result = steps.mockMvc.perform(request.accept(MediaTypes.HAL_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    private JsonNode responseBody() throws Exception {
        return steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
    }

    private User findUser(String username) {
        return users.findByUsername(username).orElseThrow();
    }

    private Leaderboard saveRanking(User user, Subject subject, int rank, int score) {
        Leaderboard entry = new Leaderboard();
        entry.setUser(user);
        entry.setSubject(subject);
        entry.setLeaderboardType(subject == null ? Leaderboard.LeaderboardType.GLOBAL : Leaderboard.LeaderboardType.SUBJECT);
        entry.setRank(rank);
        entry.setScore(score);
        entry.setCalculatedAt(Instant.now());
        Leaderboard saved = leaderboards.save(entry);
        leaderboardIds.add(saved.getId());
        return saved;
    }
}
