package cat.udl.eps.softarch.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import cat.udl.eps.softarch.demo.service.SessionService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    @Value("${allowed-origins}")
    String[] allowedOrigins;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                      LoginFailureEntryPoint loginFailureEntryPoint,
                                                      SessionService sessionService,
                                                      SessionTokenLogoutHandler sessionTokenLogoutHandler) throws Exception {
        String[] publicReads = {
                "/", "/profile", "/profile/**",
                "/subjects", "/subjects/*", "/subjects/search/findByName", "/subjects/search/findByNameContainingIgnoreCase",
                "/tests", "/tests/*", "/tests/search/findByAuthor", "/tests/search/findBySubject", "/tests/search/findByTitleContaining",
                "/tests/search/findPublicPublishedBySubject", "/tests/search/featured", // HomeFeedPathFilter maps /featuredTests here
                "/badges", "/badges/*", "/badges/search/findByName", "/badges/search/findByIsActive",
                "/leaderboards", "/leaderboards/*", "/leaderboards/search/findByLeaderboardTypeOrderByRankAsc", "/leaderboards/search/findBySubjectOrderByRankAsc", "/leaderboards/search/findByUser",
                "/leaderboards/search/globalSummary", // HomeFeedPathFilter maps /leaderboardSummary here
                "/users/*", "/users/search/findByUsernameContaining"
        };
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(HttpMethod.POST, "/password-changes").authenticated()
                .requestMatchers("/password-changes/**").denyAll()
                .requestMatchers(HttpMethod.POST, "/password-reset-requests", "/password-reset-validations", "/password-reset-confirmations").permitAll()
                .requestMatchers("/password-reset-requests/**", "/password-reset-validations/**", "/password-reset-confirmations/**").denyAll()
                .requestMatchers(HttpMethod.GET, "/identity").authenticated()
                .requestMatchers(HttpMethod.GET, "/users").authenticated()
                .requestMatchers(HttpMethod.POST, "/users").anonymous()
                .requestMatchers(HttpMethod.POST, "/users/*").denyAll()
                .requestMatchers(HttpMethod.GET, "/featuredTests", "/leaderboardSummary").permitAll()
                .requestMatchers(HttpMethod.GET, "/subjects/*", "/tests/*", "/badges/*", "/leaderboards/*").permitAll() // Public area for visitors
                .requestMatchers(HttpMethod.GET, publicReads).permitAll()
                .requestMatchers(HttpMethod.HEAD, publicReads).permitAll()
                .requestMatchers(HttpMethod.GET, "/*/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/*/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/*/*").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/*/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/*/*").authenticated()
                .anyRequest().authenticated())
            .csrf((csrf) -> csrf.disable())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors((cors) -> cors.configurationSource(corsConfigurationSource()))
            .httpBasic((httpBasic) -> httpBasic
                    .realmName(LoginFailureEntryPoint.REALM)
                    .authenticationEntryPoint(loginFailureEntryPoint))
            // Session tokens (US-AUT-06): "Authorization: Bearer <token>", issued by POST /sessions
            .addFilterBefore(new SessionTokenAuthenticationFilter(sessionService, loginFailureEntryPoint),
                    BasicAuthenticationFilter.class)
            // POST /logout deletes the session token and asks the browser to clear its cache
            .logout((logout) -> logout
                    .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/logout"))
                    .addLogoutHandler(sessionTokenLogoutHandler)
                    .addLogoutHandler(new HeaderWriterLogoutHandler(
                            new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.CACHE)))
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
