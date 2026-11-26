package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.service.PublicationCheck;
import cat.udl.eps.softarch.demo.service.TestContentService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

/**
 * Read-only sub-resource of a test (US-EST-39): the publication checklist, so the author can see
 * every requirement as passed or failed before clicking "Publish". Evaluated on the stored test.
 */
@RepositoryRestController
public class PublicationCheckController {

    private final TestRepository testRepository;
    private final TestContentService testContentService;

    public PublicationCheckController(TestRepository testRepository, TestContentService testContentService) {
        this.testRepository = testRepository;
        this.testContentService = testContentService;
    }

    @GetMapping("/tests/{id}/publicationCheck")
    public ResponseEntity<PublicationCheck> publicationCheck(@PathVariable Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean author = test.getAuthor() != null && test.getAuthor().getUsername().equals(auth.getName());
        if (!admin && !author) {
            throw new AccessDeniedException("Only the author can check the publication of the test");
        }
        return ResponseEntity.ok(testContentService.evaluate(test));
    }
}
