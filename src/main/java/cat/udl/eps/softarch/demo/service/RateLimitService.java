package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.config.RateLimitProperties;
import cat.udl.eps.softarch.demo.domain.RateLimit;
import cat.udl.eps.softarch.demo.repository.RateLimitRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Instant;

/**
 * Counts requests per key in fixed windows, using the limits in {@link RateLimitProperties}.
 * A rule that is not configured is not limited.
 */
@Service
public class RateLimitService {
    private final RateLimitRepository counters;
    private final RateLimitProperties properties;
    private final Clock clock;
    private final TransactionTemplate transaction;
    private final TransactionTemplate creation;

    public RateLimitService(RateLimitRepository counters, RateLimitProperties properties, Clock clock,
                            PlatformTransactionManager transactionManager) {
        this.counters = counters;
        this.properties = properties;
        this.clock = clock;
        this.transaction = new TransactionTemplate(transactionManager);
        this.creation = new TransactionTemplate(transactionManager);
        this.creation.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** Counts one request, or throws if the limit was already reached. */
    public void consume(String rule, String key) {
        RateLimitProperties.Rule limits = properties.getRules().get(rule);
        if (limits == null) {
            return;
        }
        String rateKey = rule + ":" + key;
        Instant now = clock.instant();
        // Make sure the row exists first, so the locking transaction below never needs a second connection.
        ensureExists(rateKey, now);
        transaction.executeWithoutResult(status -> {
            RateLimit counter = counters.lockByRateKey(rateKey).orElseThrow();
            counter.resetIfExpired(now, limits.getWindowSeconds());
            if (counter.isExhausted(limits.getLimit())) {
                throw new RateLimitExceededException(counter.secondsUntilReset(now, limits.getWindowSeconds()));
            }
            counter.increment();
            counters.save(counter);
        });
    }

    // If two requests create the same key at once, the unique rateKey rejects the second insert;
    // that is fine, the row now exists.
    private void ensureExists(String rateKey, Instant now) {
        if (counters.findByRateKey(rateKey).isPresent()) {
            return;
        }
        try {
            creation.executeWithoutResult(status -> counters.save(new RateLimit(rateKey, now)));
        } catch (DataIntegrityViolationException alreadyCreated) {
            // Another request created it first.
        }
    }
}
