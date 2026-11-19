package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Decides who may see the answer key (US-EST-18): only the author of the test and administrators.
 * Anyone else gets {@code null}, so asking for the answerKey projection reveals nothing.
 */
@Component("answerKeyVisibility")
public class AnswerKeyVisibility {

    public Boolean isCorrectFor(AnswerOption option) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return null;
        }
        if (user.getRole() == User.Role.ADMIN || user.getUsername().equals(authorOf(option))) {
            return option.getIsCorrect();
        }
        return null;
    }

    private static String authorOf(AnswerOption option) {
        if (option.getQuestion() == null || option.getQuestion().getTest() == null
                || option.getQuestion().getTest().getAuthor() == null) {
            return null;
        }
        return option.getQuestion().getTest().getAuthor().getUsername();
    }
}
