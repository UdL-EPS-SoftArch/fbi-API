package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubjectStepDefs {
    private final StepDefs stepDefs;
    private final SubjectRepository subjectRepository;
    private final LeaderboardRepository leaderboardRepository;

    public SubjectStepDefs(StepDefs stepDefs, SubjectRepository subjectRepository,
                           LeaderboardRepository leaderboardRepository) {
        this.stepDefs = stepDefs;
        this.subjectRepository = subjectRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    @Given("There is a subject with name {string}")
    public void thereIsASubjectWithName(String name) {
        if (!subjectRepository.existsByNameIgnoreCase(name)) {
            Subject subject = new Subject();
            subject.setName(name);
            subjectRepository.save(subject);
        }
    }

    @When("I create a new subject with name {string} and description {string}")
    public void iCreateANewSubject(String name, String description) throws Throwable {
        JSONObject subject = new JSONObject();
        subject.put("name", name);
        subject.put("description", description);

        stepDefs.result = stepDefs.mockMvc.perform(
                post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(subject.toString())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(AuthenticationStepDefs.authenticate()))
            .andDo(print());
    }

    @And("The subject {string} exists and is active")
    public void theSubjectExistsAndIsActive(String name) {
        List<Subject> subjects = subjectRepository.findByName(name);
        assertEquals(1, subjects.size(), "Expected exactly one subject named \"" + name + "\"");
        assertTrue(subjects.get(0).isActive(), "Subject \"" + name + "\" should be active");
    }

    @And("The subject {string} appears in the subjects list")
    public void theSubjectAppearsInTheSubjectsList(String name) throws Throwable {
        stepDefs.mockMvc.perform(
                get("/subjects")
                    .param("size", "1000")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.subjects[*].name", hasItem(name)));
    }

    @And("The subject {string} has an empty leaderboard")
    public void theSubjectHasAnEmptyLeaderboard(String name) {
        Subject subject = subjectRepository.findByName(name).get(0);
        assertTrue(leaderboardRepository.findBySubjectOrderByRankAsc(subject).isEmpty(),
                "Leaderboard of \"" + name + "\" should be empty");
    }

    @And("There is only one subject named {string}")
    public void thereIsOnlyOneSubjectNamed(String name) {
        assertEquals(1, countSubjectsNamed(name), "Expected exactly one subject named \"" + name + "\"");
    }

    private long countSubjectsNamed(String name) {
        return subjectRepository.findByNameContainingIgnoreCase(name.trim()).stream()
                .filter(s -> s.getName().trim().equalsIgnoreCase(name.trim()))
                .count();
    }
}
