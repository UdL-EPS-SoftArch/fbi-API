package cat.udl.eps.softarch.demo.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.After;
import cat.udl.eps.softarch.demo.domain.Badge;
import cat.udl.eps.softarch.demo.repository.BadgeRepository;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class AdministrativeAttemptsStepDefs {
    private final StepDefs steps;
    private final JdbcTemplate jdbc;
    private final BadgeRepository badges;
    private Badge badge;

    public AdministrativeAttemptsStepDefs(StepDefs steps, JdbcTemplate jdbc, BadgeRepository badges) {
        this.steps = steps;
        this.jdbc = jdbc;
        this.badges = badges;
    }

    @Given("An active badge rewards completion of an official test")
    public void activeBadge() {
        badge = new Badge();
        badge.setName("Administrative attempt " + java.util.UUID.randomUUID());
        badge.setRequirementType("OFFICIAL_TESTS_COMPLETED");
        badge.setRequirementValue(1);
        badge = badges.save(badge);
    }

    @After("@us-adm-15")
    public void removeBadge() {
        if (badge != null) {
            jdbc.update("DELETE FROM user_badge WHERE badge_id = ?", badge.getId());
            badges.delete(badge);
        }
    }

    @When("I search for the exact administrator username")
    public void searchAdministrator() throws Exception {
        steps.result = steps.mockMvc.perform(get("/users/search/findByUsername")
                .param("username", "admin").with(AuthenticationStepDefs.authenticate()));
    }

    @Then("The administrative attempt is graded and excluded from competition")
    public void administrativeAttemptIsGraded() throws Exception {
        var response = steps.result.andReturn().getResponse();
        assertEquals(200, response.getStatus(), response.getContentAsString());
        JSONObject attempt = new JSONObject(response.getContentAsString());
        assertEquals("SUBMITTED", attempt.getString("status"));
        assertEquals("VALIDATION", attempt.getString("attemptType"));
        assertEquals(2, attempt.getInt("score"));
        assertEquals(1, attempt.getInt("correctCount"));
        assertEquals(1, attempt.getInt("incorrectCount"));
        assertFalse(attempt.getBoolean("countsForLeaderboard"));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM leaderboard l JOIN demo_user u ON l.user_id = u.id WHERE u.username = 'admin'", Integer.class));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM user_badge b JOIN demo_user u ON b.user_id = u.id WHERE u.username = 'admin'", Integer.class));
    }
}
