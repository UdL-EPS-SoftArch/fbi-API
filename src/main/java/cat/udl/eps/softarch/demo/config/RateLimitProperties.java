package cat.udl.eps.softarch.demo.config;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Rate limits read from the {@code rate-limit} configuration prefix.
 * Rules are looked up by name: public, login, registration, recovery and reports.
 */
@Data
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {
    private Map<String, Rule> rules = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /** Maximum number of requests allowed in one window. */
        private int limit;
        /** Length of the window, in seconds. */
        private long windowSeconds;
    }
}
