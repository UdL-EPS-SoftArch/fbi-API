package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.TestPreview;
import cat.udl.eps.softarch.demo.service.TestAttemptService;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class TestPreviewEventHandler {
    private final TestAttemptService grading;

    public TestPreviewEventHandler(TestAttemptService grading) {
        this.grading = grading;
    }

    @HandleBeforeCreate
    public void grade(TestPreview preview) {
        preview.setScore(grading.gradePreview(preview, CurrentUser.get()));
    }

}
