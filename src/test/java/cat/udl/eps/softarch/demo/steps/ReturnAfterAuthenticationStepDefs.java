package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;

/**
 * US-AUT-05: the frontend sends the address where the visitor started authenticating
 * ("returnTo") and the API answers where to send them afterwards ("redirectTo").
 */
public class ReturnAfterAuthenticationStepDefs {
    private static final String HOME_PAGE = "/";

    private final StepDefs stepDefs;
    private final RegisterStepDefs registerStepDefs;
    private final TestRepository testRepository;

    public ReturnAfterAuthenticationStepDefs(StepDefs stepDefs, RegisterStepDefs registerStepDefs,
                                             TestRepository testRepository) {
        this.stepDefs = stepDefs;
        this.registerStepDefs = registerStepDefs;
        this.testRepository = testRepository;
    }

    private String detailPageOf(String title) {
        return testRepository.findByTitle(title).getFirst().getUri();
    }

    @When("I log in with {string} and password {string} returning to the detail page of the test {string}")
    public void iLogInReturningToTheDetailPageOf(String identifier, String password, String title) throws Exception {
        iLogInReturningTo(identifier, password, detailPageOf(title));
    }

    @When("I log in with {string} and password {string} returning to {string}")
    public void iLogInReturningTo(String identifier, String password, String returnTo) throws Exception {
        AuthenticationStepDefs.currentUsername = identifier;
        AuthenticationStepDefs.currentPassword = password;
        stepDefs.result = stepDefs.mockMvc.perform(
                        post("/sessions")
                                .param("returnTo", returnTo)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I register a new user with username {string}, email {string} and password {string} returning to the detail page of the test {string}")
    public void iRegisterReturningToTheDetailPageOf(String username, String email, String password, String title) throws Throwable {
        registerStepDefs.register(username, email, password,
                new JSONObject().put("acceptTerms", true).put("returnTo", detailPageOf(title)));
    }

    @Given("The test {string} is no longer available")
    public void theTestIsNoLongerAvailable(String title) {
        Test test = testRepository.findByTitle(title).getFirst();
        test.setStatus(Test.Status.WITHDRAWN);
        testRepository.save(test);
    }

    @Then("I am sent back to the detail page of the test {string}")
    public void iAmSentBackToTheDetailPageOf(String title) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.redirectTo", is(detailPageOf(title))));
    }

    @Then("I am sent to the home page")
    public void iAmSentToTheHomePage() throws Exception {
        stepDefs.result.andExpect(jsonPath("$.redirectTo", is(HOME_PAGE)));
    }

    @Then("I am told {string}")
    public void iAmTold(String notice) throws Exception {
        stepDefs.result.andExpect(jsonPath("$.notice", is(notice)));
    }
}
