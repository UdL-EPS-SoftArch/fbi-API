package cat.udl.eps.softarch.demo.config;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import cat.udl.eps.softarch.demo.domain.LeaderboardPeriod;
import cat.udl.eps.softarch.demo.repository.LeaderboardPeriodRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.util.Arrays;

@Configuration
public class DBInitialization {
    @Value("${default-password}")
    String defaultPassword;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    private final UserRepository userRepository;
    private final LeaderboardPeriodRepository periods;

    public DBInitialization(UserRepository userRepository, LeaderboardPeriodRepository periods) {
        this.userRepository = userRepository;
        this.periods = periods;
    }

    @PostConstruct
    public void initializeDatabase() {
        if (periods.findByEndsAtIsNull().isEmpty()) {
            LeaderboardPeriod period = new LeaderboardPeriod();
            periods.save(period);
        }
        // Default user
        if (!userRepository.existsByUsername("demo")) {
            User user = new User();
            user.setEmail("demo@sample.app");
            user.setUsername("demo");
            user.setPassword(defaultPassword);
            user.encodePassword();
            userRepository.save(user);
        }
        // Default administrator
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setEmail("admin@sample.app");
            admin.setUsername("admin");
            admin.setPassword(defaultPassword);
            admin.setRole(User.Role.ADMIN);
            admin.encodePassword();
            userRepository.save(admin);
        }
        if (Arrays.asList(activeProfiles.split(",")).contains("test")) {
            // Testing instances
            if (!userRepository.existsByUsername("test")) {
                User user = new User();
                user.setEmail("test@sample.app");
                user.setUsername("test");
                user.setPassword(defaultPassword);
                user.encodePassword();
                userRepository.save(user);
            }
        }
    }
}
