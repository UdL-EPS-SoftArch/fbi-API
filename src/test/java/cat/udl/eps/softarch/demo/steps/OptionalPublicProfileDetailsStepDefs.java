package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OptionalPublicProfileDetailsStepDefs {
    private final StepDefs steps;
    private final AuthenticationStepDefs authentication;
    private final UserRepository users;

    public OptionalPublicProfileDetailsStepDefs(StepDefs steps, AuthenticationStepDefs authentication,
            UserRepository users) {
        this.steps = steps;
        this.authentication = authentication;
        this.users = users;
    }

    @Given("the optional profile of {string} has full name {string}")
    public void setFullName(String username, String fullName) {
        User user = users.findByUsername(username).orElseThrow();
        user.setFullName(fullName);
        users.save(user);
    }

    @Given("the optional profile viewer is {string}")
    public void setViewer(String viewer) {
        if (viewer.equals("visitor")) {
            authentication.iMNotLoggedIn();
        } else {
            authentication.iLoginAsWithPassword(viewer, "password");
        }
    }

    @Then("the optional profile search hides personal details")
    public void searchHidesDetails() throws Exception {
        steps.result.andExpect(MockMvcResultMatchers.status().isOk());
        JsonNode response = steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
        JsonNode profiles = response.path("_embedded").path("users");
        assertEquals(1, profiles.size());
        JsonNode profile = profiles.get(0);
        assertEquals("optional-profile", profile.path("username").asString());
        assertTrue(profile.path("fullName").isMissingNode() || profile.path("fullName").isNull());
        assertTrue(profile.path("email").isMissingNode() || profile.path("email").isNull());
    }

    @Then("the publication settings are show full name {word} and show email {word}")
    public void checkPublicationSettings(String showFullName, String showEmail) throws Exception {
        steps.result.andExpect(jsonPath("$.showFullName").value(Boolean.parseBoolean(showFullName)))
                .andExpect(jsonPath("$.showEmail").value(Boolean.parseBoolean(showEmail)));
    }

    @After("@US-EST-42")
    public void cleanup() {
        users.findByUsername("optional-profile").ifPresent(users::delete);
    }
}
