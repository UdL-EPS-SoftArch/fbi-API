package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "featuredTest", types = Test.class)
public interface FeaturedTestProjection {
    String getTitle();
    Test.TestType getTestType();
    SubjectExcerpt getSubject();

    interface SubjectExcerpt {
        String getName();
    }
}
