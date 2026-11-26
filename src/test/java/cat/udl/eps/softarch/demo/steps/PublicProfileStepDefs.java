package cat.udl.eps.softarch.demo.steps;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import cat.udl.eps.softarch.demo.domain.Badge;
import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.domain.UserBadge;
import cat.udl.eps.softarch.demo.repository.BadgeRepository;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.SubjectRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserBadgeRepository;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.http.MediaType;

public class PublicProfileStepDefs {
    private final StepDefs steps;
    private final AuthenticationStepDefs authentication;
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final BadgeRepository badges;
    private final UserBadgeRepository userBadges;
    private final LeaderboardRepository leaderboards;
    private final TestRepository tests;
    private final TestAttemptRepository attempts;
    private final List<Long> badgeIds = new ArrayList<>();
    private final List<Long> userBadgeIds = new ArrayList<>();
    private final List<Long> rankingIds = new ArrayList<>();
    private final List<Long> testIds = new ArrayList<>();
    private Subject subject;
    private TestAttempt attempt;

    public PublicProfileStepDefs(StepDefs steps, AuthenticationStepDefs authentication, UserRepository users,
            SubjectRepository subjects, BadgeRepository badges, UserBadgeRepository userBadges,
            LeaderboardRepository leaderboards, TestRepository tests, TestAttemptRepository attempts) {
        this.steps = steps;
        this.authentication = authentication;
        this.users = users;
        this.subjects = subjects;
        this.badges = badges;
        this.userBadges = userBadges;
        this.leaderboards = leaderboards;
        this.tests = tests;
        this.attempts = attempts;
    }

    @Given("the public profile of {string} has name {string} and avatar {string}")
    public void setPublicProfileDetails(String username, String fullName, String avatar) {
        User student = findUser(username);
        student.setFullName(fullName);
        student.setProfilePicture(avatar);
        users.save(student);
    }

    @Given("{string} has badges, leaderboard positions, tests and a completed attempt")
    public void createProfileContent(String username) {
        User student = findUser(username);
        subject = new Subject();
        subject.setName("Public profile subject");
        subject = subjects.save(subject);

        awardBadge(student, "Visible profile badge", true);
        awardBadge(student, "Hidden profile badge", false);
        addRanking(student, Leaderboard.LeaderboardType.GLOBAL, null, 2);
        addRanking(student, Leaderboard.LeaderboardType.SUBJECT, subject, 3);

        Test publicTest = addTest(student, "Published public custom test", Test.TestType.CUSTOM, Test.Status.PUBLISHED, true);
        addTest(student, "Published private custom test", Test.TestType.CUSTOM, Test.Status.PUBLISHED, false);
        addTest(student, "Draft public custom test", Test.TestType.CUSTOM, Test.Status.DRAFT, true);
        addTest(student, "Withdrawn public custom test", Test.TestType.CUSTOM, Test.Status.WITHDRAWN, true);
        addTest(student, "Published official test", Test.TestType.OFFICIAL, Test.Status.PUBLISHED, true);
        addTest(findUser("demo"), "Another student's public custom test", Test.TestType.CUSTOM, Test.Status.PUBLISHED, true);

        attempt = new TestAttempt();
        attempt.setTest(publicTest);
        attempt.setUser(student);
        attempt.setStatus(TestAttempt.Status.SUBMITTED);
        attempt.setAttemptNumber(1);
        attempt.setScore(71);
        attempt.setCorrectCount(3);
        attempt.setIncorrectCount(1);
        attempt.setStartedAt(Instant.parse("2025-01-01T00:00:00Z"));
        attempt.setSubmittedAt(Instant.parse("2025-01-01T00:01:00Z"));
        attempt = attempts.save(attempt);
    }

    @Given("{string} lets the public profile show their full name {word} and email {word}")
    public void setDetailVisibility(String username, String showFullName, String showEmail) {
        User student = findUser(username);
        student.setShowFullName(Boolean.parseBoolean(showFullName));
        student.setShowEmail(Boolean.parseBoolean(showEmail));
        users.save(student);
    }

    @Given("{string} makes their public profile private")
    public void makeProfilePrivate(String username) {
        User student = findUser(username);
        student.setProfilePublic(false);
        users.save(student);
    }

    @Given("{string} chooses anonymity in leaderboards")
    public void makeRankingsAnonymous(String username) {
        User student = findUser(username);
        student.setAnonymousInLeaderboards(true);
        users.save(student);
    }

    @Given("the public profile viewer is {string}")
    public void selectViewer(String viewer) {
        if ("visitor".equals(viewer)) {
            authentication.iMNotLoggedIn();
        } else {
            authentication.iLoginAsWithPassword(viewer, "password");
        }
    }

    @When("I open the public profile of {string}")
    public void openPublicProfile(String username) throws Exception {
        steps.result = steps.mockMvc.perform(get("/users/search/publicProfile")
                .param("username", username)
                .accept(MediaType.APPLICATION_JSON)
                .with(AuthenticationStepDefs.authenticate()));
    }

    @Then("the public profile shows {string} with avatar {string}")
    public void checkPublicIdentity(String username, String avatar) throws Exception {
        steps.result.andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.profilePublic").value(true))
                .andExpect(jsonPath("$.profilePicture").value(avatar));
    }

    @Then("the only badge shown on the public profile is {string}")
    public void checkBadge(String badge) throws Exception {
        steps.result.andExpect(jsonPath("$.badges", hasSize(1)))
                .andExpect(jsonPath("$.badges[*].name", contains(badge)));
    }

    @Then("the public profile shows global rank {int} and rank {int} for {string}")
    public void checkRankings(int globalRank, int subjectRank, String subjectName) throws Exception {
        steps.result.andExpect(jsonPath("$.leaderboardPositions", hasSize(2)))
                .andExpect(jsonPath("$.leaderboardPositions[?(@.leaderboardType == 'GLOBAL')].rank", contains(globalRank)))
                .andExpect(jsonPath("$.leaderboardPositions[?(@.leaderboardType == 'SUBJECT')].rank", contains(subjectRank)))
                .andExpect(jsonPath("$.leaderboardPositions[?(@.leaderboardType == 'SUBJECT')].subjectName", contains(subjectName)));
    }

    @Then("the only custom test shown on the public profile is {string}")
    public void checkCustomTest(String title) throws Exception {
        steps.result.andExpect(jsonPath("$.customTests", hasSize(1)))
                .andExpect(jsonPath("$.customTests[*].title", contains(title)));
    }

    @Then("the public profile hides {string}")
    public void checkHiddenField(String field) throws Exception {
        steps.result.andExpect(jsonPath("$." + field).doesNotExist());
    }

    @Then("the public profile exposes no attempt history or detailed results")
    public void checkNoAttemptHistory() throws Exception {
        for (String field : List.of("attempts", "testAttempts", "attemptHistory", "testHistory", "results",
                "detailedResults", "attemptNumber", "correctCount", "incorrectCount", "submittedAt")) {
            steps.result.andExpect(jsonPath("$.." + field).doesNotHaveJsonPath());
        }
        String body = steps.result.andReturn().getResponse().getContentAsString();
        assertFalse(body.contains(attempt.getUri()), "The public profile links to a private attempt");
    }

    @Then("the public profile only identifies {string} as private")
    public void checkPrivateProfile(String username) throws Exception {
        steps.result.andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.profilePublic").value(false));
        var body = steps.mapper.readTree(steps.result.andReturn().getResponse().getContentAsString());
        Set<String> allowedFields = Set.of("username", "profilePublic", "_links");
        for (var field : body.properties()) {
            assertTrue(allowedFields.contains(field.getKey()) || field.getValue().isNull(),
                    () -> "Private profile exposes " + field.getKey());
        }
        var links = body.path("_links");
        for (var link : links.properties()) {
            assertTrue("self".equals(link.getKey()), () -> "Private profile links to " + link.getKey());
        }
    }

    @Then("the public profile has no leaderboard positions")
    public void checkNoRankings() throws Exception {
        steps.result.andExpect(jsonPath("$.leaderboardPositions", hasSize(0)));
    }

    @After("@US-COM-08")
    public void removeProfileFixtures() {
        if (attempt != null && attempt.getId() != null) {
            attempts.deleteById(attempt.getId());
        }
        tests.deleteAllById(testIds);
        leaderboards.deleteAllById(rankingIds);
        userBadges.deleteAllById(userBadgeIds);
        badges.deleteAllById(badgeIds);
        if (subject != null && subject.getId() != null) {
            subjects.deleteById(subject.getId());
        }
        users.findByUsername("public-profile-student").ifPresent(users::delete);
    }

    private User findUser(String username) {
        return users.findByUsername(username).orElseThrow();
    }

    private void awardBadge(User student, String name, boolean visible) {
        Badge badge = new Badge();
        badge.setName(name);
        badge.setRequirementType("OFFICIAL_TESTS_COMPLETED");
        badge.setRequirementValue(1);
        badge = badges.save(badge);
        badgeIds.add(badge.getId());

        UserBadge earnedBadge = new UserBadge();
        earnedBadge.setUser(student);
        earnedBadge.setBadge(badge);
        earnedBadge.setIsVisible(visible);
        earnedBadge.setEarnedAt(Instant.parse("2025-01-01T00:00:00Z"));
        userBadgeIds.add(userBadges.save(earnedBadge).getId());
    }

    private void addRanking(User student, Leaderboard.LeaderboardType type, Subject rankingSubject, int rank) {
        Leaderboard entry = new Leaderboard();
        entry.setUser(student);
        entry.setLeaderboardType(type);
        entry.setSubject(rankingSubject);
        entry.setRank(rank);
        entry.setScore(100);
        rankingIds.add(leaderboards.save(entry).getId());
    }

    private Test addTest(User author, String title, Test.TestType type, Test.Status status, boolean isPublic) {
        Test test = new Test();
        test.setSubject(subject);
        test.setAuthor(author);
        test.setTitle(title);
        test.setTestType(type);
        test.setStatus(status);
        test.setIsPublic(isPublic);
        if (status == Test.Status.PUBLISHED) {
            test.setPublishedAt(Instant.parse("2025-01-01T00:00:00Z"));
        }
        Test saved = tests.save(test);
        testIds.add(saved.getId());
        return saved;
    }
}
