package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

public class PublishTestStepDefs {
    private final StepDefs stepDefs;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public PublishTestStepDefs(StepDefs stepDefs, UserRepository userRepository,
                               SubjectRepository subjectRepository, TestRepository testRepository,
                               QuestionRepository questionRepository,
                               AnswerOptionRepository answerOptionRepository) {
        this.stepDefs = stepDefs;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    @Given("There is a subject named {string}")
    public void thereIsASubjectNamed(String name) {
        if (subjectRepository.findByName(name).isEmpty()) {
            Subject subject = new Subject();
            subject.setName(name);
            subjectRepository.save(subject);
        }
    }

    @And("I have a draft {string} test {string} in subject {string}")
    public void iHaveADraftTestInSubject(String type, String title, String subjectName) {
        Test test = new Test();
        test.setTitle(title);
        test.setSubject(subjectRepository.findByName(subjectName).getFirst());
        test.setAuthor(userRepository.findByUsername(AuthenticationStepDefs.currentUsername).orElseThrow());
        test.setTestType(Test.TestType.valueOf(type));
        test.setStatus(Test.Status.DRAFT);
        testRepository.save(test);
    }

    // The question is created through the API so that its event handlers run (max score);
    // its options are stored directly, they are not under test here
    @When("I add a question to test {string} worth {int} points with {int} options, {int} of them correct")
    public void iAddAQuestionToTest(String title, int score, int options, int correct) throws Throwable {
        Test test = findTest(title);
        JSONObject question = new JSONObject()
                .put("test", test.getUri())
                .put("questionText", "Question worth " + score)
                .put("score", score);

        String location = stepDefs.mockMvc.perform(
                post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(question.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        Long questionId = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        Question created = questionRepository.listByTestId(test.getId()).stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst().orElseThrow();
        for (int i = 0; i < options; i++) {
            AnswerOption option = new AnswerOption();
            option.setQuestion(created);
            option.setOptionText("Option " + (i + 1));
            option.setIsCorrect(i < correct);
            option.setOrderIndex(i);
            answerOptionRepository.save(option);
        }
    }

    @When("I publish the test {string} with public visibility {string}")
    public void iPublishTheTest(String title, String isPublic) throws Throwable {
        JSONObject changes = new JSONObject()
                .put("status", "PUBLISHED")
                .put("isPublic", Boolean.parseBoolean(isPublic));

        stepDefs.result = stepDefs.mockMvc.perform(
                patch(findTest(title).getUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changes.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @When("I open the publication check of test {string}")
    public void iOpenThePublicationCheck(String title) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                get(findTest(title).getUri() + "/publicationCheck")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }

    @And("The publication check is not publishable")
    public void thePublicationCheckIsNotPublishable() throws Throwable {
        stepDefs.result.andExpect(jsonPath("$.publishable").value(false));
    }

    @And("The requirement {string} is passed")
    public void theRequirementIsPassed(String message) throws Throwable {
        stepDefs.result.andExpect(jsonPath(
                "$.requirements[?(@.message == '" + message + "' && @.passed == true)]").isNotEmpty());
    }

    @And("The requirement {string} is failed and points to question {int}")
    public void theRequirementIsFailedAndPointsToQuestion(String message, int number) throws Throwable {
        stepDefs.result.andExpect(jsonPath(
                "$.requirements[?(@.message == '" + message + "' && @.passed == false "
                        + "&& @.target.type == 'QUESTION' && @.target.number == " + number + ")]").isNotEmpty());
    }

    @And("The requirement {string} is failed and points to the field {string}")
    public void theRequirementIsFailedAndPointsToTheField(String message, String field) throws Throwable {
        stepDefs.result.andExpect(jsonPath(
                "$.requirements[?(@.message == '" + message + "' && @.passed == false "
                        + "&& @.target.type == 'TEST' && @.target.field == '" + field + "')]").isNotEmpty());
    }

    @Then("The test {string} has a maximum score of {int}")
    public void theTestHasAMaximumScoreOf(String title, int maxScore) {
        assertEquals(maxScore, findTest(title).getMaxScore());
    }

    @And("The test {string} has status {string}")
    public void theTestHasStatus(String title, String status) {
        assertEquals(Test.Status.valueOf(status), findTest(title).getStatus());
    }

    private Test findTest(String title) {
        return testRepository.findByTitle(title).getFirst();
    }
}
