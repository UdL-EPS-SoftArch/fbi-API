package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * {@code ?projection=answerKey}: the option with whether it is correct, shown only to the test
 * author and administrators (null for anyone else). The default view never includes it.
 */
@Projection(name = "answerKey", types = AnswerOption.class)
public interface AnswerKeyProjection {
    String getOptionText();

    Integer getOrderIndex();

    @Value("#{@answerKeyVisibility.isCorrectFor(target)}")
    Boolean getIsCorrect();
}
