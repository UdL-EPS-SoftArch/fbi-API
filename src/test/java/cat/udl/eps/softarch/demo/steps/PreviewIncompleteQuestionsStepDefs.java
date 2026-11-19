package cat.udl.eps.softarch.demo.steps;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class PreviewIncompleteQuestionsStepDefs {
    private final StepDefs steps;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final TestRepository tests;
    private final QuestionRepository questions;
    private final AnswerOptionRepository options;
    private Long testId;
    private Test test;
    private final List<Long> incompleteIds = new ArrayList<>();

    public PreviewIncompleteQuestionsStepDefs(StepDefs steps, UserRepository users, SubjectRepository subjects,
            TestRepository tests, QuestionRepository questions, AnswerOptionRepository options) {
        this.steps = steps;
        this.users = users;
        this.subjects = subjects;
        this.tests = tests;
        this.questions = questions;
        this.options = options;
    }

    @Given("a draft preview test with complete and incomplete questions")
    public void draftWithQuestions() {
        createTest("draft");
        addQuestion(0, 2, 1, false);
        addQuestion(1, 1, 1, true);
        addQuestion(2, 2, 0, true);
        addQuestion(3, 2, 2, true);
    }

    @Given("a {word} preview test")
    public void simpleTest(String fixture) {
        createTest(fixture);
        if (fixture.equals("published") || fixture.equals("draft")) addQuestion(0, 1, 0, true);
    }

    @Given("a empty draft preview test")
    public void emptyTest() { createTest("draft"); }

    private void createTest(String fixture) {
        if (fixture.equals("nonexistent")) {
            testId = Long.MAX_VALUE;
            return;
        }
        Subject subject = new Subject();
        subject.setName("Preview " + UUID.randomUUID());
        subject = subjects.save(subject);
        test = new Test();
        test.setSubject(subject);
        test.setAuthor(users.findByUsername("demo").orElseThrow());
        test.setTitle("Preview " + UUID.randomUUID());
        test.setStatus(fixture.equals("published") ? Test.Status.PUBLISHED : Test.Status.DRAFT);
        test = tests.save(test);
        testId = test.getId();
    }

    private void addQuestion(int index, int count, int correct, boolean incomplete) {
        Question question = new Question();
        question.setTest(test);
        question.setQuestionText("Preview question " + index);
        question.setOrderIndex(index);
        question = questions.save(question);
        if (incomplete) incompleteIds.add(question.getId());
        for (int i = 0; i < count; i++) {
            AnswerOption option = new AnswerOption();
            option.setQuestion(question);
            option.setOptionText("Option " + i);
            option.setIsCorrect(i < correct);
            option.setOrderIndex(i);
            options.save(option);
        }
    }

    @When("I request incomplete questions for the preview test")
    public void request() throws Exception {
        steps.result = steps.mockMvc.perform(get("/questions/search/incompleteForPreview")
                .param("testId", testId.toString()).accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    private JSONArray returnedQuestions() throws Exception {
        JSONObject body = new JSONObject(steps.result.andReturn().getResponse().getContentAsString());
        JSONObject embedded = body.optJSONObject("_embedded");
        JSONArray result = embedded == null ? null : embedded.optJSONArray("questions");
        return result == null ? new JSONArray() : result;
    }

    @Then("the incomplete preview questions are in the expected order")
    public void expectedQuestions() throws Exception {
        JSONArray result = returnedQuestions();
        assertEquals(incompleteIds.size(), result.length());
        for (int i = 0; i < result.length(); i++) {
            JSONObject question = result.getJSONObject(i);
            assertTrue(question.getJSONObject("_links").getJSONObject("self").getString("href")
                    .endsWith("/" + incompleteIds.get(i)));
            assertFalse(question.has("isCorrect"));
        }
    }

    @Then("the incomplete preview questions are empty")
    public void emptyQuestions() throws Exception { assertEquals(0, returnedQuestions().length()); }
}
