package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.config.RateLimitProperties;
import cat.udl.eps.softarch.demo.repository.RateLimitRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;

public class RateLimitStepDefs {
    private static final int TOO_MANY_REQUESTS = 429;

    private final StepDefs stepDefs;
    private final RateLimitProperties properties;
    private final RateLimitRepository counters;
    private Map<String, RateLimitProperties.Rule> originalRules;

    public RateLimitStepDefs(StepDefs stepDefs, RateLimitProperties properties, RateLimitRepository counters) {
        this.stepDefs = stepDefs;
        this.properties = properties;
        this.counters = counters;
    }

    // Only rate limiting scenarios start from empty counters and may change the limits.
    @Before("@ratelimit")
    public void resetRateLimits() {
        counters.deleteAll();
        originalRules = new HashMap<>(properties.getRules());
    }

    @After("@ratelimit")
    public void restoreRateLimits() {
        if (originalRules != null) {
            properties.setRules(originalRules);
        }
    }

    @Given("The {string} rate limit is {int} requests per {int} seconds")
    public void setRateLimit(String name, int limit, int windowSeconds) {
        properties.getRules().put(name, new RateLimitProperties.Rule(limit, windowSeconds));
    }

    // Uses the current credentials: anonymous unless the scenario has logged in.
    @When("I request {string} {int} time(s)")
    public void requestRepeatedly(String path, int times) throws Exception {
        for (int i = 0; i < times; i++) {
            stepDefs.result = stepDefs.mockMvc.perform(get(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()));
            if (i < times - 1) {
                stepDefs.result.andExpect(status().is(not(TOO_MANY_REQUESTS)));
            }
        }
    }

    // Always sends a wrong password, whatever credentials the scenario has stored.
    @When("I fail to login as {string} {int} times")
    public void failToLogin(String username, int times) throws Exception {
        for (int i = 0; i < times; i++) {
            stepDefs.result = stepDefs.mockMvc.perform(get("/identity")
                    .accept(MediaType.APPLICATION_JSON)
                    .with(httpBasic(username, "wrong-password")));
            if (i < times - 1) {
                stepDefs.result.andExpect(status().isUnauthorized());
            }
        }
    }

    @When("I submit {int} report(s)")
    public void submitReports(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            stepDefs.result = stepDefs.mockMvc.perform(post("/reports")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"reason\":\"Rate limit test report\"}")
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()));
            if (i < count - 1) {
                stepDefs.result.andExpect(status().isCreated());
            }
        }
    }

    @Then("The response has a Retry-After of {int} seconds")
    public void checkRetryAfter(int seconds) throws Exception {
        stepDefs.result.andExpect(header().string("Retry-After", String.valueOf(seconds)));
    }
}
