package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.repository.FeaturedTestProjection;
import cat.udl.eps.softarch.demo.repository.LeaderboardSummaryProjection;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class HomeFeedProjectionConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.getProjectionConfiguration().addProjection(FeaturedTestProjection.class);
        config.getProjectionConfiguration().addProjection(LeaderboardSummaryProjection.class);
    }
}
