package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

public class RegisterStepDefs {
  private final StepDefs stepDefs;
  private final UserRepository userRepository;

  public RegisterStepDefs(StepDefs stepDefs, UserRepository userRepository) {
    this.stepDefs = stepDefs;
    this.userRepository = userRepository;
  }

  @Given("^There is no registered user with username \"([^\"]*)\"$")
  public void thereIsNoRegisteredUserWithUsername(String user) {
    assertFalse(userRepository.existsByUsername(user), "User \"" + user + "\"shouldn't exist");
  }

  @Given("^There is a registered user with username \"([^\"]*)\" and password \"([^\"]*)\" and email \"([^\"]*)\"$")
  public void thereIsARegisteredUserWithUsernameAndPasswordAndEmail(String username, String password, String email) {
    if (!userRepository.existsByUsername(username)) {
      User user = new User();
      user.setEmail(email);
      user.setUsername(username);
      user.setPassword(password);
      user.encodePassword();
      userRepository.save(user);
    }
  }

  @And("^I can login with username \"([^\"]*)\" and password \"([^\"]*)\"$")
  public void iCanLoginWithUsernameAndPassword(String username, String password) throws Throwable {
    AuthenticationStepDefs.currentUsername = username;
    AuthenticationStepDefs.currentPassword = password;

    stepDefs.result = stepDefs.mockMvc.perform(
        get("/identity", username)
            .accept(MediaType.APPLICATION_JSON)
            .with(AuthenticationStepDefs.authenticate()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @And("^I cannot login with username \"([^\"]*)\" and password \"([^\"]*)\"$")
  public void iCannotLoginWithUsernameAndPassword(String username, String password) throws Throwable {
    AuthenticationStepDefs.currentUsername = username;
    AuthenticationStepDefs.currentPassword = password;

    stepDefs.result = stepDefs.mockMvc.perform(
        get("/identity", username)
            .accept(MediaType.APPLICATION_JSON)
            .with(AuthenticationStepDefs.authenticate()))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @When("^I register a new user with username \"([^\"]*)\", email \"([^\"]*)\" and password \"([^\"]*)\"$")
  public void iRegisterANewUserWithUsernameEmailAndPassword(String username, String email, String password) throws Throwable {
    register(username, email, password, new JSONObject().put("acceptTerms", true));
  }

  @When("^I register a new user with username \"([^\"]*)\", email \"([^\"]*)\" and password \"([^\"]*)\" without accepting the terms$")
  public void iRegisterANewUserWithoutAcceptingTheTerms(String username, String email, String password) throws Throwable {
    register(username, email, password, new JSONObject());
  }

  @When("^I register a new user with username \"([^\"]*)\", email \"([^\"]*)\" and password \"([^\"]*)\" choosing the role \"([^\"]*)\"$")
  public void iRegisterANewUserChoosingTheRole(String username, String email, String password, String role) throws Throwable {
    register(username, email, password, new JSONObject().put("acceptTerms", true).put("role", role));
  }

  @And("^The user \"([^\"]*)\" has the \"([^\"]*)\" role$")
  public void theUserHasTheRole(String username, String role) {
    User user = userRepository.findByUsername(username).orElseThrow();
    assertEquals(User.Role.valueOf(role), user.getRole());
  }

  private void register(String username, String email, String password, JSONObject extraFields) throws Throwable {
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);

    // Private profile fields are not serialized outside an owner/admin request,
    // so email, password and the extra fields are set on the raw JSON
    JSONObject json = new JSONObject(stepDefs.mapper.writeValueAsString(user))
            .put("email", email)
            .put("password", password);
    for (var keys = extraFields.keys(); keys.hasNext(); ) {
      String key = String.valueOf(keys.next());
      json.put(key, extraFields.get(key));
    }

    stepDefs.result = stepDefs.mockMvc.perform(
            post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print());
  }

  @And("^It has been created a user with username \"([^\"]*)\" and email \"([^\"]*)\", the password is not returned$")
  public void itHasBeenCreatedAUserWithUsername(String username, String email) throws Throwable {
    User user = userRepository.findByUsername(username).orElseThrow();
    stepDefs.result = stepDefs.mockMvc.perform(
            get(user.getUri())
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print())
            .andExpect(jsonPath("$.username", is(username)))
            .andExpect(jsonPath("$.email", is(email)))
            .andExpect(jsonPath("$.password").doesNotExist());
  }

  @And("^It has not been created a user with username \"([^\"]*)\"$")
  public void itHasNotBeenCreatedAUserWithUsername(String username) {
    assertFalse(userRepository.existsByUsername(username), "User \"" + username + "\" shouldn't exist");
  }
}
