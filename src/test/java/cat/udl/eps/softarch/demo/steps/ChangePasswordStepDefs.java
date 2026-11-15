package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ChangePasswordStepDefs {
    private final StepDefs steps;
    private final UserRepository users;
    private final RecoveryTestConfiguration.TestMailbox mailbox;

    public ChangePasswordStepDefs(StepDefs steps, UserRepository users, RecoveryTestConfiguration.TestMailbox mailbox) {
        this.steps = steps;
        this.users = users;
        this.mailbox = mailbox;
    }

    @Given("The account {string} is a {string} for password change")
    public void setRole(String username, String role) {
        User user = users.findByUsername(username).orElseThrow();
        user.setRole(User.Role.valueOf(role));
        users.save(user);
    }

    @When("I change my password from {string} to {string}")
    public void change(String current, String next) throws Exception {
        steps.result = steps.mockMvc.perform(post("/password-changes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(steps.mapper.writeValueAsString(Map.of("currentPassword", current, "newPassword", next)))
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Then("The password change response contains no passwords")
    public void noPasswordsInResponse() throws Exception {
        String body = steps.result.andReturn().getResponse().getContentAsString();
        assertFalse(body.contains("oldpassword"));
        assertFalse(body.contains("newpassword"));
    }

    @Then("A password change notification is sent to {string}")
    public void notification(String email) throws Exception {
        SimpleMailMessage message = mailbox.messages.poll(5, TimeUnit.SECONDS);
        assertNotNull(message);
        assertArrayEquals(new String[]{email}, message.getTo());
        assertFalse(message.getText().contains("oldpassword"));
        assertFalse(message.getText().contains("newpassword"));
        assertNull(mailbox.messages.poll(300, TimeUnit.MILLISECONDS));
    }

    @When("I attempt to change my password through a {string} profile update")
    public void updateProfile(String method) throws Exception {
        User user = users.findByUsername("changeuser").orElseThrow();
        var request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .request(org.springframework.http.HttpMethod.valueOf(method), user.getUri())
                .contentType(MediaType.APPLICATION_JSON)
                .content(steps.mapper.writeValueAsString(Map.of(
                        "username", "changeuser", "email", "change@example.com",
                        "password", "newpassword")))
                .with(AuthenticationStepDefs.authenticate());
        steps.result = steps.mockMvc.perform(request);
    }

    @Then("The profile password update is rejected")
    public void profileUpdateRejected() throws Exception {
        assertEquals(400, steps.result.andReturn().getResponse().getStatus());
    }

    @Then("No password change notification is sent")
    public void noNotification() throws Exception {
        assertNull(mailbox.messages.poll(300, TimeUnit.MILLISECONDS));
    }
}
