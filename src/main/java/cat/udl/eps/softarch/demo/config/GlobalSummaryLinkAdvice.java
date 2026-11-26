package cat.udl.eps.softarch.demo.config;

import org.springframework.core.MethodParameter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import cat.udl.eps.softarch.demo.domain.Leaderboard;
import java.util.ArrayList;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
public class GlobalSummaryLinkAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof CollectionModel<?> collection) {
            var items = new ArrayList<Object>();
            boolean changed = false;
            for (Object item : collection.getContent()) {
                if (item instanceof PersistentEntityResource resource
                        && resource.getTarget() instanceof Leaderboard entry
                        && Boolean.TRUE.equals(entry.getUser().getLeaderboardAnonymous())) {
                    items.add(EntityModel.of(new AnonymousLeaderboardEntry(entry.getRank(), entry.getScore())));
                    changed = true;
                } else {
                    items.add(item);
                }
            }
            if (changed) {
                body = collection instanceof PagedModel<?> page
                        ? PagedModel.of(items, page.getMetadata(), collection.getLinks())
                        : CollectionModel.of(items, collection.getLinks());
            }
        }
        if ((request.getURI().getPath().endsWith("/leaderboardSummary")
                || request.getURI().getPath().endsWith("/leaderboards/search/globalSummary"))
                && body instanceof RepresentationModel<?> model) {
            String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/leaderboards/search/findByLeaderboardTypeOrderByRankAsc")
                    .queryParam("type", "GLOBAL").build().toUriString();
            model.add(Link.of(uri, "globalLeaderboard"));
        }
        return body;
    }

    @Relation(itemRelation = "leaderboard", collectionRelation = "leaderboards")
    private record AnonymousLeaderboardEntry(Integer rank, Integer score) {
        public String getUsername() { return "Anonymous student"; }
    }
}
