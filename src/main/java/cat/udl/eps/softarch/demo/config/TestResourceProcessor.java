package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

/** Advertises the publication checklist (US-EST-39) of every test. */
@Component
public class TestResourceProcessor implements RepresentationModelProcessor<EntityModel<Test>> {
    @Override
    public EntityModel<Test> process(EntityModel<Test> model) {
        model.getLink("self").ifPresent(self ->
                model.add(Link.of(self.getHref() + "/publicationCheck", "publicationCheck")));
        return model;
    }
}
