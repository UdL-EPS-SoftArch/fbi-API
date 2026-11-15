package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Recomputes the SUBJECT and GLOBAL leaderboard entries from the stored attempts (2.4.2).
 * Entries are derived data: they are never edited by hand, only recalculated.
 * Call it after a competitive attempt is submitted and when an official test is
 * published, withdrawn or republished.
 */
@Service
public class LeaderboardService {

    private final TestAttemptRepository testAttemptRepository;
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(TestAttemptRepository testAttemptRepository,
                              LeaderboardRepository leaderboardRepository) {
        this.testAttemptRepository = testAttemptRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    /** Best attempt of one user on one test, plus what it cost to reach it (tie-breakers). */
    private static final class Best {
        final User user;
        final Subject subject;
        int score = -1;
        int attemptsToBest;
        long secondsToBest;
        int attemptsSoFar;
        long secondsSoFar;

        Best(User user, Subject subject) {
            this.user = user;
            this.subject = subject;
        }
    }

    /** Sum of the best results of one user in one scope (a subject, or everything). */
    private static final class Totals {
        final User user;
        final Subject subject; // null for GLOBAL
        int score;
        int attempts;
        long seconds;

        Totals(User user, Subject subject) {
            this.user = user;
            this.subject = subject;
        }

        void add(Best best) {
            score += best.score;
            attempts += best.attemptsToBest;
            seconds += best.secondsToBest;
        }
    }

    @Transactional
    public void recalculateAll() {
        // Ordered by user, test, submission time: the first attempt with a strictly higher score
        // becomes the personal best; equal scores later on do not replace it.
        Map<String, Best> bests = new LinkedHashMap<>();
        for (TestAttempt attempt : testAttemptRepository.listEligibleForRanking()) {
            String key = attempt.getUser().getId() + ":" + attempt.getTest().getId();
            Best best = bests.computeIfAbsent(key,
                    k -> new Best(attempt.getUser(), attempt.getTest().getSubject()));
            best.attemptsSoFar++;
            best.secondsSoFar += attempt.getDurationSeconds() == null ? 0 : attempt.getDurationSeconds();
            int score = attempt.getScore() == null ? 0 : attempt.getScore();
            if (score > best.score) {
                best.score = score;
                best.attemptsToBest = best.attemptsSoFar;
                best.secondsToBest = best.secondsSoFar;
            }
        }

        Map<String, Totals> subjectTotals = new LinkedHashMap<>();
        Map<Long, Totals> globalTotals = new LinkedHashMap<>();
        for (Best best : bests.values()) {
            subjectTotals.computeIfAbsent(best.user.getId() + ":" + best.subject.getId(),
                    k -> new Totals(best.user, best.subject)).add(best);
            globalTotals.computeIfAbsent(best.user.getId(),
                    k -> new Totals(best.user, null)).add(best);
        }

        Map<String, Leaderboard> existing = new HashMap<>();
        List<Leaderboard> all = new ArrayList<>();
        for (Leaderboard entry : leaderboardRepository.findAll()) {
            all.add(entry);
            existing.putIfAbsent(key(entry.getLeaderboardType(), entry.getUser(), entry.getSubject()), entry);
        }

        Set<Leaderboard> used = Collections.newSetFromMap(new IdentityHashMap<>());
        Instant now = Instant.now();
        store(subjectTotals.values(), Leaderboard.LeaderboardType.SUBJECT, existing, used, now);
        store(globalTotals.values(), Leaderboard.LeaderboardType.GLOBAL, existing, used, now);

        // Users that are no longer eligible (withdrawn test, ...) disappear from the ranking
        for (Leaderboard entry : all) {
            if (!used.contains(entry)) {
                leaderboardRepository.delete(entry);
            }
        }
    }

    private void store(Collection<Totals> totals, Leaderboard.LeaderboardType type,
                       Map<String, Leaderboard> existing, Set<Leaderboard> used, Instant now) {
        List<Totals> sorted = new ArrayList<>(totals);
        // Higher score, fewer attempts, less time; username only fixes the display order
        sorted.sort(Comparator.<Totals>comparingInt(t -> -t.score)
                .thenComparingInt(t -> t.attempts)
                .thenComparingLong(t -> t.seconds)
                .thenComparing(t -> t.user.getUsername()));

        int rank = 0;
        Totals previous = null;
        for (int i = 0; i < sorted.size(); i++) {
            Totals current = sorted.get(i);
            // Same rank only when score and both tie-breakers are equal
            if (previous == null
                    || current.score != previous.score
                    || current.attempts != previous.attempts
                    || current.seconds != previous.seconds) {
                rank = i + 1;
            }
            previous = current;

            Leaderboard entry = existing.get(key(type, current.user, current.subject));
            if (entry == null) {
                entry = new Leaderboard();
                entry.setUser(current.user);
                entry.setSubject(current.subject);
                entry.setLeaderboardType(type);
            }
            entry.setScore(current.score);
            entry.setRank(rank);
            entry.setCalculatedAt(now);
            leaderboardRepository.save(entry);
            used.add(entry);
        }
    }

    private static String key(Leaderboard.LeaderboardType type, User user, Subject subject) {
        return type + ":" + (user == null ? 0 : user.getId()) + ":" + (subject == null ? 0 : subject.getId());
    }
}
