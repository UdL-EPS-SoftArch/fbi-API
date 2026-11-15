package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.Badge;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.domain.UserBadge;
import cat.udl.eps.softarch.demo.repository.BadgeRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.repository.UserBadgeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Awards badges automatically after an official-test submission (2.4.6).
 * Students never create or edit UserBadge records through the API.
 */
@Service
public class BadgeService {

    /**
     * Requirement types the system knows how to measure. They are PROPOSED values: the
     * requirements document says they still need team approval. Badge.requirementType must
     * match one of these names; any other value is ignored (never awarded).
     */
    public enum RequirementType {
        /** Number of different official tests completed. */
        OFFICIAL_TESTS_COMPLETED,
        /** Number of official tests with a perfect attempt (every question correct). */
        PERFECT_SCORES,
        /** Sum of the best score obtained in each official test. */
        OFFICIAL_SCORE_TOTAL
    }

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final TestAttemptRepository testAttemptRepository;

    public BadgeService(BadgeRepository badgeRepository,
                        UserBadgeRepository userBadgeRepository,
                        TestAttemptRepository testAttemptRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.testAttemptRepository = testAttemptRepository;
    }

    // Not @Transactional on purpose: a duplicate-award race must not mark a wider transaction as rollback-only
    public void evaluate(User user) {
        if (user == null || user.getRole() != User.Role.STUDENT) {
            return; // administrators cannot earn badges
        }

        Set<Long> completedTests = new HashSet<>();
        Set<Long> perfectTests = new HashSet<>();
        Map<Long, Integer> bestByTest = new HashMap<>();
        for (TestAttempt attempt : testAttemptRepository.listSubmittedOfficialByUserId(user.getId())) {
            Long testId = attempt.getTest().getId();
            completedTests.add(testId);
            if (attempt.getCorrectCount() != null && attempt.getCorrectCount() > 0
                    && isZero(attempt.getIncorrectCount()) && isZero(attempt.getUnansweredCount())) {
                perfectTests.add(testId);
            }
            bestByTest.merge(testId, attempt.getScore() == null ? 0 : attempt.getScore(), Math::max);
        }
        long scoreTotal = bestByTest.values().stream().mapToLong(Integer::longValue).sum();

        List<Badge> activeBadges = badgeRepository.findByIsActive(true);
        for (Badge badge : activeBadges) {
            RequirementType type = parse(badge.getRequirementType());
            if (type == null || badge.getRequirementValue() == null) {
                continue;
            }
            long measured = switch (type) {
                case OFFICIAL_TESTS_COMPLETED -> completedTests.size();
                case PERFECT_SCORES -> perfectTests.size();
                case OFFICIAL_SCORE_TOTAL -> scoreTotal;
            };
            if (measured >= badge.getRequirementValue()
                    && userBadgeRepository.countOwned(user.getId(), badge.getId()) == 0) {
                award(user, badge);
            }
        }
    }

    private void award(User user, Badge badge) {
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadge.setEarnedAt(Instant.now());
        userBadge.setIsVisible(true);
        try {
            userBadgeRepository.save(userBadge);
        } catch (DataIntegrityViolationException alreadyAwarded) {
            // Unique (user, badge): another request awarded it first, nothing to do
        }
    }

    private static boolean isZero(Integer value) {
        return value == null || value == 0;
    }

    private static RequirementType parse(String value) {
        if (value == null) {
            return null;
        }
        try {
            return RequirementType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException unknown) {
            return null;
        }
    }
}
