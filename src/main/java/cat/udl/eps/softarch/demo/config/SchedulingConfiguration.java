package cat.udl.eps.softarch.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Enables scheduled tasks, such as the daily deletion of unverified accounts (US-AUT-07). */
@Configuration
@EnableScheduling
public class SchedulingConfiguration {
}
