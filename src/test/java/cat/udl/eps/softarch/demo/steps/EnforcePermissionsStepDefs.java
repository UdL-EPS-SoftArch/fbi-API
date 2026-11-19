package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Map;

import cat.udl.eps.softarch.demo.domain.Badge;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.BadgeRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

public class EnforcePermissionsStepDefs {
    private final StepDefs stepDefs;
    private final AttemptHistoryIntegrityStepDefs attempts;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final BadgeRepository badgeRepository;
    private final JdbcTemplate jdbc;

    public EnforcePermissionsStepDefs(StepDefs stepDefs, AttemptHistoryIntegrityStepDefs attempts,
                                      UserRepository userRepository, TestRepository testRepository,
                                      BadgeRepository badgeRepository, JdbcTemplate jdbc) {
        this.stepDefs = stepDefs;
        this.attempts = attempts;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.badgeRepository = badgeRepository;
        this.jdbc = jdbc;
    }

    @When("I retrieve the {string} of that attempt")
    public void iRetrieveTheResourceOfThatAttempt(String resource) throws Throwable {
        String uri = switch (resource) {
            case "attempt" -> attempts.getAttempt().getUri();
            case "answer" -> attempts.getAnswer().getUri();
            default -> throw new IllegalArgumentException("Unknown resource " + resource);
        };
        stepDefs.result = stepDefs.mockMvc.perform(
                get(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I link the user of that attempt to {string}")
    public void iLinkTheUserOfThatAttemptTo(String username) throws Throwable {
        performLink(attempts.getAttempt().getUri() + "/user", user(username).getUri());
    }

    @When("I link the author of test {string} to {string}")
    public void iLinkTheAuthorOfTestTo(String title, String username) throws Throwable {
        performLink(test(title).getUri() + "/author", user(username).getUri());
    }

    @When("I change the score of that attempt to {int}")
    public void iChangeTheScoreOfThatAttemptTo(int score) throws Throwable {
        performPatch(attempts.getAttempt().getUri(), Map.of("score", score));
    }

    @Then("That attempt has no score")
    public void thatAttemptHasNoScore() {
        Integer score = jdbc.queryForObject("SELECT score FROM test_attempt WHERE id = ?",
                Integer.class, attempts.getAttempt().getId());
        assertNull(score, "A student must not be able to set the score of an attempt");
    }

    @When("I change my role to {string}")
    public void iChangeMyRoleTo(String role) throws Throwable {
        performPatch(user(AuthenticationStepDefs.currentUsername).getUri(), Map.of("role", role));
    }

    @When("I award the user {string} a badge")
    public void iAwardTheUserABadge(String username) throws Throwable {
        Badge badge = new Badge();
        badge.setName("Self-awarded");
        badge.setRequirementType("TESTS_COMPLETED");
        badge = badgeRepository.save(badge);
        stepDefs.result = stepDefs.mockMvc.perform(
                post("/userBadges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stepDefs.mapper.writeValueAsString(
                                Map.of("user", user(username).getUri(), "badge", badge.getUri())))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I change the title of test {string} to {string}")
    public void iChangeTheTitleOfTestTo(String title, String newTitle) throws Throwable {
        performPatch(test(title).getUri(), Map.of("title", newTitle));
    }

    private User user(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    private Test test(String title) {
        return testRepository.findByTitle(title).getFirst();
    }

    // Association endpoints take the target resource as a "text/uri-list" body
    private void performLink(String associationUri, String targetUri) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                put(associationUri)
                        .contentType("text/uri-list")
                        .content(targetUri)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    private void performPatch(String uri, Map<String, ?> body) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stepDefs.mapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }
}
