package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.PasswordResetRateLimit;
import cat.udl.eps.softarch.demo.repository.PasswordResetRateLimitRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;

@Configuration
@EnableAsync
public class PasswordResetConfiguration {
    @Bean
    ApplicationRunner initializePasswordResetLock(PasswordResetRateLimitRepository limits, Clock clock) {
        return args -> {
            if (!limits.existsById("GLOBAL")) {
                try {
                    limits.saveAndFlush(new PasswordResetRateLimit("GLOBAL", clock.instant()));
                } catch (DataIntegrityViolationException concurrentInitialization) {
                    if (!limits.existsById("GLOBAL")) {
                        throw concurrentInitialization;
                    }
                }
            }
        };
    }

    @Bean
    Clock passwordResetClock() {
        return Clock.systemUTC();
    }
}
