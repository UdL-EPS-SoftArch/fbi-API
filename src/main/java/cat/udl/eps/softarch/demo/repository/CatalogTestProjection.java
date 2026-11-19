package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * A test as listed in the public catalog (US-COM-03): title, subject name, type, number of
 * questions and year. Use it with ?projection=catalogTest; the self link of each test leads
 * to its public detail page (US-COM-05).
 */
@Projection(name = "catalogTest", types = Test.class)
public interface CatalogTestProjection {
    String getTitle();

    Test.TestType getTestType();

    Integer getYear();

    SubjectExcerpt getSubject();

    @Value("#{@questionRepository.countByTestId(target.id)}")
    long getQuestionCount();

    interface SubjectExcerpt {
        String getName();
    }
}
