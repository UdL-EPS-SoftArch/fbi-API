package cat.udl.eps.softarch.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HomeFeedPathFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String target = switch (path) {
            case "/featuredTests", "/tests/search/featured" -> "/tests/search/featured";
            case "/leaderboardSummary", "/leaderboards/search/globalSummary" -> "/leaderboards/search/globalSummary";
            default -> null;
        };
        if (target == null || !"GET".equals(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String projection = target.equals("/tests/search/featured") ? "featuredTest" : "leaderboardSummary";
        HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                return "projection".equals(name) ? projection : super.getParameter(name);
            }

            @Override
            public String[] getParameterValues(String name) {
                return "projection".equals(name) ? new String[]{projection} : super.getParameterValues(name);
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                Map<String, String[]> parameters = new HashMap<>(super.getParameterMap());
                parameters.put("projection", new String[]{projection});
                return parameters;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return Collections.enumeration(getParameterMap().keySet());
            }

            @Override
            public String getRequestURI() {
                return getContextPath() + target;
            }

            @Override
            public String getServletPath() {
                return target;
            }
        };
        chain.doFilter(wrapped, response);
    }
}
