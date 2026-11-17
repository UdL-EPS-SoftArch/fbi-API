package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;

public class LogInStepDefs {
    private final StepDefs stepDefs;
    private final UserRepository userRepository;
    private final RecoveryTestConfiguration.MutableClock clock;

    public LogInStepDefs(StepDefs stepDefs, UserRepository userRepository,
                         RecoveryTestConfiguration.MutableClock clock) {
        this.stepDefs = stepDefs;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    // The frontend logs in by sending the credentials to /identity, which returns the user and its role
    @When("I log in with {string} and password {string}")
    public void iLogInWith(String identifier, String password) throws Exception {
        AuthenticationStepDefs.currentUsername = identifier;
        AuthenticationStepDefs.currentPassword = password;
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/identity")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @Given("I failed to log in {int} times with {string}")
    public void iFailedToLogIn(int times, String identifier) throws Exception {
        for (int i = 0; i < times; i++) {
            iLogInWith(identifier, "wrongpassword");
            stepDefs.result.andExpect(status().isUnauthorized());
        }
    }

    @Then("I am logged in as {string} with role {string}")
    public void iAmLoggedInAs(String username, String role) throws Exception {
        stepDefs.result
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.role", is(role)));
    }

    @Given("{int} minutes have passed")
    public void minutesHavePassed(int minutes) {
        clock.advanceSeconds(minutes * 60L);
    }

    @Given("The account {string} has not verified its email")
    public void theAccountHasNotVerifiedItsEmail(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setEmailVerified(false);
        userRepository.save(user);
    }

    @Given("The account {string} is suspended")
    public void theAccountIsSuspended(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setSuspended(true);
        userRepository.save(user);
    }
}
