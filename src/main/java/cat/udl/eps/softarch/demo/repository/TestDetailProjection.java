package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

/**
 * Public detail page of a test (US-COM-05): title, subject name, year, type, difficulty, tags,
 * number of questions and, for custom tests, the author's public name. Use it with
 * ?projection=testDetail. It never includes questions, answer options or correct answers.
 */
@Projection(name = "testDetail", types = Test.class)
public interface TestDetailProjection {
    String getTitle();

    SubjectExcerpt getSubject();

    Integer getYear();

    Test.TestType getTestType();

    Test.Difficulty getDifficulty();

    Set<String> getTags();

    @Value("#{@questionRepository.countByTestId(target.id)}")
    long getQuestionCount();

    /** Public name (username) of the author; only shown for custom tests. */
    @Value("#{target.testType.name() == 'CUSTOM' && target.author != null ? target.author.username : null}")
    String getAuthorName();

    interface SubjectExcerpt {
        String getName();
    }
}
