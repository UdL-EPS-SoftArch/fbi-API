package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import cat.udl.eps.softarch.demo.service.EmailVerificationService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyEmailStepDefs {
    private static final String SUBJECT = "Verify your email address";

    private final StepDefs stepDefs;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final RecoveryTestConfiguration.TestMailbox mailbox;
    private final RecoveryTestConfiguration.MutableClock clock;

    // Tokens of the links received in this scenario, in order
    private final List<String> emailedTokens = new ArrayList<>();

    public VerifyEmailStepDefs(StepDefs stepDefs, UserRepository userRepository,
                               EmailVerificationService emailVerificationService,
                               RecoveryTestConfiguration.TestMailbox mailbox,
                               RecoveryTestConfiguration.MutableClock clock) {
        this.stepDefs = stepDefs;
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
        this.mailbox = mailbox;
        this.clock = clock;
    }

    @Before
    public void clearTokens() {
        emailedTokens.clear();
    }

    @Then("A verification link is sent to {string}")
    public void aVerificationLinkIsSentTo(String email) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        SimpleMailMessage message;
        do {
            message = mailbox.messages.poll(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
            assertNotNull(message, "No verification email received for " + email);
        } while (!email.equals(message.getTo()[0]) || !SUBJECT.equals(message.getSubject()));
        String body = message.getText();
        assertNotNull(body);
        int start = body.indexOf("token=");
        assertTrue(start >= 0, "No token in the verification link");
        emailedTokens.add(body.substring(start + 6).split("[&\\s]")[0]);
    }

    @Then("No verification email is sent to {string}")
    public void noVerificationEmailIsSentTo(String email) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(500);
        while (System.nanoTime() < deadline) {
            SimpleMailMessage message = mailbox.messages.poll(
                    Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
            if (message == null) {
                return;
            }
            assertNotEquals(email, message.getTo()[0]);
        }
    }

    @When("I open the emailed verification link")
    public void iOpenTheEmailedVerificationLink() throws Exception {
        assertFalse(emailedTokens.isEmpty(), "No verification link was received");
        openLink(emailedTokens.getLast());
    }

    @When("I open the first verification link")
    public void iOpenTheFirstVerificationLink() throws Exception {
        assertFalse(emailedTokens.isEmpty(), "No verification link was received");
        openLink(emailedTokens.getFirst());
    }

    private void openLink(String token) throws Exception {
        postJson("/email-verifications", Map.of("token", token));
    }

    @When("I request a new verification link for {string}")
    public void iRequestANewVerificationLinkFor(String email) throws Exception {
        postJson("/email-verification-requests", Map.of("email", email));
    }

    private void postJson(String path, Map<String, String> body) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(
                        post(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(stepDefs.mapper.writeValueAsString(body))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @Given("{int} hours have passed")
    public void hoursHavePassed(int hours) {
        clock.advanceSeconds(Duration.ofHours(hours).toSeconds());
    }

    @Given("There is an unverified user {string} with email {string} registered {int} days ago")
    public void thereIsAnUnverifiedUser(String username, String email, int days) {
        createUser(username, email, false, days);
    }

    @Given("There is a verified user {string} with email {string} registered {int} days ago")
    public void thereIsAVerifiedUser(String username, String email, int days) {
        createUser(username, email, true, days);
    }

    private void createUser(String username, String email, boolean verified, int daysAgo) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.encodePassword();
        user.setEmailVerified(verified);
        user = userRepository.save(user);
        // The registration date is set by auditing on creation; move it to the past afterwards
        user.setCreatedAt(clock.instant().minus(Duration.ofDays(daysAgo)));
        userRepository.save(user);
    }

    @When("The daily cleanup of unverified accounts runs")
    public void theDailyCleanupRuns() {
        emailVerificationService.deleteStaleUnverifiedAccounts();
    }

    @Then("The user {string} no longer exists")
    public void theUserNoLongerExists(String username) {
        assertFalse(userRepository.existsByUsername(username), "User \"" + username + "\" should have been deleted");
    }
    // "The user {string} still exists" is defined in UserPrivacyStepDefs
}
