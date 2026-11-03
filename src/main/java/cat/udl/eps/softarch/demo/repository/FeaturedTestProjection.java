package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PlatformTest;
import cat.udl.eps.softarch.demo.domain.Subject;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "featuredTest", types = PlatformTest.class)
public interface FeaturedTestProjection {
    String getTitle();
    PlatformTest.Type getType();
    Subject getSubject();
}
