package cat.udl.eps.softarch.demo.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import cat.udl.eps.softarch.demo.domain.AnswerAttempt;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.repository.AnswerAttemptRepository;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;

import java.time.Instant;

public class AttemptHistoryIntegrityStepDefs {
    private final StepDefs stepDefs;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final AnswerAttemptRepository answerAttemptRepository;

    private Question question;
    private TestAttempt attempt;
    private AnswerAttempt answer;

    public AttemptHistoryIntegrityStepDefs(StepDefs stepDefs, UserRepository userRepository,
                                           SubjectRepository subjectRepository, TestRepository testRepository,
                                           QuestionRepository questionRepository,
                                           TestAttemptRepository testAttemptRepository,
                                           AnswerAttemptRepository answerAttemptRepository) {
        this.stepDefs = stepDefs;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.answerAttemptRepository = answerAttemptRepository;
    }

    @Given("There is a published official test {string} with one question")
    public void thereIsAPublishedOfficialTestWithOneQuestion(String title) {
        Subject subject = new Subject();
        subject.setName("Software Architecture");
        subject = subjectRepository.save(subject);

        Test test = new Test();
        test.setSubject(subject);
        test.setAuthor(userRepository.findByUsername("admin").orElseThrow());
        test.setTitle(title);
        test.setTestType(Test.TestType.OFFICIAL);
        test.setStatus(Test.Status.PUBLISHED);
        test.setIsPublic(true);
        test = testRepository.save(test);

        question = new Question();
        question.setTest(test);
        question.setQuestionText("What does REST stand for?");
        question = questionRepository.save(question);
    }

    @Given("{string} has a {string} attempt with one answer on test {string}")
    public void hasAnAttemptWithOneAnswerOnTest(String username, String status, String title) {
        attempt = new TestAttempt();
        attempt.setUser(userRepository.findByUsername(username).orElseThrow());
        attempt.setTest(question.getTest());
        attempt.setStatus(TestAttempt.Status.valueOf(status));
        attempt.setStartedAt(Instant.now());
        attempt = testAttemptRepository.save(attempt);

        answer = new AnswerAttempt();
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer = answerAttemptRepository.save(answer);
    }

    @When("I delete that attempt")
    public void iDeleteThatAttempt() throws Throwable {
        performDelete(attempt.getUri());
    }

    @When("I delete the answer of that attempt")
    public void iDeleteTheAnswerOfThatAttempt() throws Throwable {
        performDelete(answer.getUri());
    }

    @And("The attempt and its answer still exist")
    public void theAttemptAndItsAnswerStillExist() {
        assertTrue(testAttemptRepository.existsById(attempt.getId()), "The attempt should still exist");
        assertTrue(answerAttemptRepository.existsById(answer.getId()), "The answer should still exist");
    }

    private void performDelete(String uri) throws Throwable {
        stepDefs.result = stepDefs.mockMvc.perform(
                delete(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(AuthenticationStepDefs.authenticate()))
                .andDo(print());
    }
}
