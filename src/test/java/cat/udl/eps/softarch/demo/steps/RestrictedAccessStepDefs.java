package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

public class RestrictedAccessStepDefs {
    private final StepDefs stepDefs;
    private final SubjectRepository subjectRepository;
    private final TestRepository testRepository;

    public RestrictedAccessStepDefs(StepDefs stepDefs, SubjectRepository subjectRepository, TestRepository testRepository) {
        this.stepDefs = stepDefs;
        this.subjectRepository = subjectRepository;
        this.testRepository = testRepository;
    }

    @When("I send an anonymous {string} request to {string}")
    public void sendAnonymousRequest(String method, String path) throws Exception {
        MockHttpServletRequestBuilder request = method.equals("POST")
                ? post(path).contentType(MediaType.APPLICATION_JSON).content("{}") : get(path);
        stepDefs.result = stepDefs.mockMvc.perform(request.accept(MediaType.APPLICATION_JSON));
    }

    @And("The response has no body or redirect")
    public void responseHasNoBodyOrRedirect() throws Exception {
        stepDefs.result.andExpect(content().string(""))
                .andExpect(header().doesNotExist("Location"));
    }

    @When("I create a subject through the API")
    public void createSubject() throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(post("/subjects")
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Restricted access subject\"}")
                .with(AuthenticationStepDefs.authenticate()));
    }
    @Given("There is a private test {string}")
    public void createPrivateTest(String title) {
        Subject subject = new Subject();
        subject.setName("Private catalog subject");
        subject = subjectRepository.save(subject);
        Test test = new Test();
        test.setSubject(subject);
        test.setTitle(title);
        test.setStatus(Test.Status.PUBLISHED);
        test.setIsPublic(false);
        testRepository.save(test);
    }

    @Then("The catalog contains {string} but not {string}")
    public void checkCatalog(String visible, String hidden) throws Exception {
        stepDefs.result.andExpect(content().string(containsString(visible)))
                .andExpect(content().string(not(containsString(hidden))));
    }

    @When("A configured origin sends a preflight request for POST {string}")
    public void sendPreflight(String path) throws Exception {
        stepDefs.result = stepDefs.mockMvc.perform(options(path)
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"));
    }

    @And("The preflight permits the configured origin and POST")
    public void checkPreflight() throws Exception {
        stepDefs.result.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("authorization")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("content-type")));
    }
}
