package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;

public class LogOutStepDefs {
    private final StepDefs stepDefs;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;

    private String sessionToken;
    private TestAttempt attempt;

    public LogOutStepDefs(StepDefs stepDefs, UserRepository userRepository, TestRepository testRepository,
                          TestAttemptRepository testAttemptRepository) {
        this.stepDefs = stepDefs;
        this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
    }

    private RequestPostProcessor withSession() {
        return request -> {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sessionToken);
            return request;
        };
    }

    @When("I start a session with {string} and password {string}")
    public void iStartASessionWith(String identifier, String password) throws Exception {
        AuthenticationStepDefs.currentUsername = identifier;
        AuthenticationStepDefs.currentPassword = password;
        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/sessions")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @Then("I receive a session token")
    public void iReceiveASessionToken() throws Exception {
        stepDefs.result
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.expiresAt", notNullValue()));
        sessionToken = JsonPath.read(stepDefs.result.andReturn().getResponse().getContentAsString(), "$.token");
        // From now on the requests only carry the session token, never the password
        AuthenticationStepDefs.currentUsername = AuthenticationStepDefs.currentPassword = null;
    }

    @Given("I have a session as {string} with password {string}")
    public void iHaveASessionAs(String identifier, String password) throws Exception {
        iStartASessionWith(identifier, password);
        stepDefs.result.andExpect(status().isCreated());
        iReceiveASessionToken();
    }

    @When("I request my identity with my session")
    public void iRequestMyIdentityWithMySession() throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get("/identity")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(withSession()))
                .andDo(print());
    }

    @When("I log out")
    public void iLogOut() throws Exception {
        // HTTPS: browsers only honour Clear-Site-Data on secure connections
        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/logout")
                                .secure(true)
                                .with(withSession()))
                .andDo(print());
    }

    @When("I start a session with my session token")
    public void iStartASessionWithMySessionToken() throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/sessions")
                                .accept(MediaType.APPLICATION_JSON)
                                .with(withSession()))
                .andDo(print());
    }

    @Then("The browser is asked to clear its cache")
    public void theBrowserIsAskedToClearItsCache() throws Exception {
        stepDefs.result.andExpect(header().string("Clear-Site-Data", containsString("\"cache\"")));
    }

    @Then("The response must not be cached")
    public void theResponseMustNotBeCached() throws Exception {
        stepDefs.result.andExpect(header().string(HttpHeaders.CACHE_CONTROL, containsString("no-store")));
    }

    @Given("{string} has an attempt in progress on test {string}")
    public void hasAnAttemptInProgress(String username, String title) {
        attempt = new TestAttempt();
        attempt.setUser(userRepository.findByUsername(username).orElseThrow());
        attempt.setTest(testRepository.findByTitle(title).getFirst());
        attempt.setStatus(TestAttempt.Status.IN_PROGRESS);
        attempt.setStartedAt(Instant.now());
        attempt = testAttemptRepository.save(attempt);
    }

    @When("I retrieve my attempt in progress with my session")
    public void iRetrieveMyAttemptInProgress() throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        get(attempt.getUri())
                                .accept(MediaType.APPLICATION_JSON)
                                .with(withSession()))
                .andDo(print());
    }

    @Then("The attempt is still in progress")
    public void theAttemptIsStillInProgress() throws Exception {
        stepDefs.result.andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }
}
