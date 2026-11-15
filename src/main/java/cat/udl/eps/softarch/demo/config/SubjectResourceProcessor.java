package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.Subject;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SubjectResourceProcessor implements RepresentationModelProcessor<EntityModel<Subject>> {
    @Override
    public EntityModel<Subject> process(EntityModel<Subject> model) {
        String self = model.getRequiredLink("self").getHref();
        String base = self.substring(0, self.indexOf("/subjects/"));
        String tests = UriComponentsBuilder.fromUriString(base + "/tests/search/findPublicPublishedBySubject")
                .queryParam("subject", self).build().encode().toUriString();
        String leaderboard = UriComponentsBuilder.fromUriString(base + "/leaderboards/search/findBySubjectOrderByRankAsc")
                .queryParam("subject", self).build().encode().toUriString();
        model.add(Link.of(tests, "publishedTests"));
        model.add(Link.of(leaderboard, "leaderboard"));
        return model;
    }
}
