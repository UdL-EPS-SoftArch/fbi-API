package cat.udl.eps.softarch.demo.config;

import org.springframework.core.MethodParameter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
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
}
