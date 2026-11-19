package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.SearchText;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fills the search title (US-COM-04) of the tests saved before it existed, so that they also
 * appear in keyword searches. Does nothing once every test has one.
 */
@Component
public class SearchTitleBackfill implements ApplicationRunner {
    private final TestRepository tests;

    public SearchTitleBackfill(TestRepository tests) {
        this.tests = tests;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Test test : tests.findBySearchTitleIsNull()) {
            test.setSearchTitle(SearchText.normalize(test.getTitle()));
            tests.save(test);
        }
    }
}
