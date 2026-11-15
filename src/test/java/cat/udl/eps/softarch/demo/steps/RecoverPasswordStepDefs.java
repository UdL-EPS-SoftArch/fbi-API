package cat.udl.eps.softarch.demo.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;
import io.cucumber.java.Before;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecoverPasswordStepDefs {
    private final StepDefs steps;
    private String previousResponse;
    private String emailedToken;
    private List<Integer> concurrentStatuses;
    private final RecoveryTestConfiguration.TestMailbox mailbox;
    private final RecoveryTestConfiguration.MutableClock clock;
    private final UserRepository users;

    public RecoverPasswordStepDefs(StepDefs steps, RecoveryTestConfiguration.TestMailbox mailbox,
                                   RecoveryTestConfiguration.MutableClock clock, UserRepository users) {
        this.steps = steps;
        this.mailbox = mailbox;
        this.clock = clock;
        this.users = users;
    }

    @Before
    public void clearMailbox() {
        mailbox.messages.clear();
    }

    @Then("A recovery link is sent to {string}")
    public void receiveLink(String email) throws Exception {
        SimpleMailMessage message;
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        do {
            message = mailbox.messages.poll(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
            assertNotNull(message, "No recovery email received for " + email);
        } while (!email.equals(message.getTo()[0]));
        String body = message.getText();
        assertNotNull(body);
        int start = body.indexOf("token=");
        org.junit.jupiter.api.Assertions.assertTrue(start >= 0, "No token in recovery link");
        emailedToken = body.substring(start + 6).split("[&\\s]")[0];
    }

    @Then("No recovery email is sent to {string}")
    public void noEmailForUnknownAccount(String email) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(500);
        while (System.nanoTime() < deadline) {
            SimpleMailMessage message = mailbox.messages.poll(
                    Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
            if (message == null) {
                return;
            }
            org.junit.jupiter.api.Assertions.assertNotEquals(email, message.getTo()[0]);
        }
    }

    @When("I validate the emailed recovery token")
    public void validateEmailedToken() throws Exception {
        validateToken(emailedToken);
    }

    @When("I confirm the emailed token with password {string}")
    public void confirmEmailedToken(String password) throws Exception {
        confirmToken(emailedToken, password);
    }

    @When("The recovery clock advances by {int} seconds")
    public void advanceClock(int seconds) {
        clock.advanceSeconds(seconds);
    }

    @Given("The account {string} is an administrator")
    public void makeAdmin(String username) {
        User user = users.findByUsername(username).orElseThrow();
        user.setRole(User.Role.ADMIN);
        users.save(user);
    }

    @Then("The account {string} has role {string} and email {string}")
    public void accountIsPreserved(String username, String role, String email) {
        User user = users.findByUsername(username).orElseThrow();
        assertEquals(role, user.getRole().name());
        assertEquals(email, user.getEmail());
    }

    @When("Two clients confirm the emailed token concurrently")
    public void confirmConcurrently() throws Exception {
        var start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Integer> confirm = () -> {
                start.await();
                return steps.mockMvc.perform(post("/password-reset-confirmations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(steps.mapper.writeValueAsString(Map.of(
                                "token", emailedToken, "newPassword", "newpassword"))))
                        .andReturn().getResponse().getStatus();
            };
            Future<Integer> first = executor.submit(confirm);
            Future<Integer> second = executor.submit(confirm);
            start.countDown();
            concurrentStatuses = List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS));
        }
    }

    @Then("One confirmation succeeds and the other reports an expired token")
    public void oneConfirmationSucceeds() {
        assertEquals(List.of(201, 410), concurrentStatuses.stream().sorted().toList());
    }

    private void postJson(String path, Map<String, String> body) throws Exception {
        steps.result = steps.mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(steps.mapper.writeValueAsString(body)));
    }

    @When("I request password recovery for {string}")
    public void requestRecovery(String email) throws Exception {
        postJson("/password-reset-requests", Map.of("email", email));
    }

    @When("I save the recovery response and request recovery for {string}")
    public void saveAndRequest(String email) throws Exception {
        previousResponse = steps.result.andReturn().getResponse().getContentAsString();
        requestRecovery(email);
    }

    @Then("The recovery responses are identical")
    public void responsesAreIdentical() throws Exception {
        assertEquals(previousResponse, steps.result.andReturn().getResponse().getContentAsString());
    }

    @When("I request password recovery {int} times for {string}")
    public void requestRepeatedly(int count, String email) throws Exception {
        for (int i = 0; i < count; i++) {
            requestRecovery(email);
            if (i < count - 1) {
                steps.result.andExpect(status().isCreated());
            }
        }
    }

    @When("I validate the recovery token {string}")
    public void validateToken(String token) throws Exception {
        postJson("/password-reset-validations", Map.of("token", token));
    }

    @When("I confirm the recovery token {string} with password {string}")
    public void confirmToken(String token, String password) throws Exception {
        postJson("/password-reset-confirmations", Map.of("token", token, "newPassword", password));
    }
}
