package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;

public class UserPrivacyStepDefs {
    private final StepDefs stepDefs;
    private final UserRepository userRepository;

    public UserPrivacyStepDefs(StepDefs stepDefs, UserRepository userRepository) {
        this.stepDefs = stepDefs;
        this.userRepository = userRepository;
    }

    @When("I retrieve the profile of user {string}")
    public void iRetrieveTheProfileOfUser(String username) throws Throwable {
        User user = userRepository.findByUsername(username).orElseThrow();
        stepDefs.result = stepDefs.mockMvc.perform(
                get(user.getUri())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("The profile field {string} is {string}")
    public void theProfileFieldIs(String field, String value) throws Throwable {
        stepDefs.result.andExpect(jsonPath("$." + field, is(value)));
    }

    @And("The profile field {string} is hidden")
    public void theProfileFieldIsHidden(String field) throws Throwable {
        stepDefs.result.andExpect(jsonPath("$." + field).value(nullValue()));
    }

    @When("I search users whose username contains {string}")
    public void iSearchUsersWhoseUsernameContains(String text) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                get("/users/search/findByUsernameContaining")
                        .param("text", text)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @Then("The user search returns {int} users without visible emails")
    public void theUserSearchReturnsUsersWithoutVisibleEmails(int count) throws Throwable {
        stepDefs.result
                .andExpect(jsonPath("$..users[*].username", hasSize(count)))
                .andExpect(jsonPath("$..users[*].email", everyItem(nullValue())));
    }

    @When("I delete the user {string}")
    public void iDeleteTheUser(String username) throws Throwable {
        User user = userRepository.findByUsername(username).orElseThrow();
        stepDefs.result = stepDefs.mockMvc.perform(
                delete(user.getUri())
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("The user {string} still exists")
    public void theUserStillExists(String username) {
        assertTrue(userRepository.existsByUsername(username), "User \"" + username + "\" should still exist");
    }
}
