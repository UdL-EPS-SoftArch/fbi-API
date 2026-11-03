package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.PlatformTest;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.PlatformTestRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FeaturedTestsStepDefs {
    private final StepDefs steps;
    private final PlatformTestRepository tests;
    private final SubjectRepository subjects;

    public FeaturedTestsStepDefs(StepDefs steps, PlatformTestRepository tests, SubjectRepository subjects) {
        this.steps = steps;
        this.tests = tests;
        this.subjects = subjects;
    }

    private void create(String title, String subject, String type, boolean published, boolean featured, boolean publicVisible) {
        PlatformTest test = new PlatformTest();
        test.setTitle(title);
        Subject testSubject = new Subject();
        testSubject.setName(subject);
        test.setSubject(subjects.save(testSubject));
        test.setType(PlatformTest.Type.valueOf(type));
        test.setPublished(published);
        test.setFeatured(featured);
        test.setPublicVisible(publicVisible);
        tests.save(test);
    }

    @Given("a published featured test {string} in subject {string} of type {string}")
    public void publishedFeatured(String title, String subject, String type) {
        tests.deleteAll();
        create(title, subject, type, true, true, true);
    }

    @Given("an unpublished featured test {string} in subject {string} of type {string}")
    public void unpublishedFeatured(String title, String subject, String type) {
        create(title, subject, type, false, true, true);
    }

    @Given("a published test {string} in subject {string} of type {string} that is not featured")
    public void publishedUnfeatured(String title, String subject, String type) {
        create(title, subject, type, true, false, true);
    }

    @Given("a private published featured test {string} in subject {string} of type {string}")
    public void privateFeatured(String title, String subject, String type) {
        create(title, subject, type, true, true, false);
    }

    @Given("there are no tests")
    public void noTests() {
        tests.deleteAll();
    }

    @Then("the featured feed is empty")
    public void checkEmptyFeed() throws Exception {
        steps.result.andExpect(jsonPath("$._embedded.platformTests", hasSize(0)));
    }

    @When("a visitor requests featured tests")
    public void requestFeatured() throws Exception {
        steps.result = steps.mockMvc.perform(get("/platformTests/search/featured"));
    }

    @Then("the featured feed contains only {string} with subject {string} and type {string}")
    public void checkFeed(String title, String subject, String type) throws Exception {
        steps.result.andExpect(jsonPath("$._embedded.platformTests", hasSize(1)))
                .andExpect(jsonPath("$._embedded.platformTests[0].title").value(title))
                .andExpect(jsonPath("$._embedded.platformTests[0].subject.name").value(subject))
                .andExpect(jsonPath("$._embedded.platformTests[0].type").value(type));
    }
}
