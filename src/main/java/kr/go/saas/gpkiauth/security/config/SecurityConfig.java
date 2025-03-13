package kr.go.saas.gpkiauth.security.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.go.saas.gpkiauth.security.endpoint.SessionAttributeNames;
import kr.go.saas.gpkiauth.security.entrypoint.OAuth2LoginAuthenticationEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper,
            AuthenticationFailureHandler failureHandler,
            AuthenticationEntryPoint entryPoint
    ) throws Exception {
        OAuth2AuthorizationServerConfigurer configurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http
                .securityMatcher(configurer.getEndpointsMatcher())
                .with(configurer, (authorizationServer) -> authorizationServer
                        .oidc((oidc) -> oidc
                                .userInfoEndpoint((userInfo) -> userInfo
                                        .userInfoMapper(userInfoMapper)
                                        .errorResponseHandler(failureHandler)
                                )
                                .logoutEndpoint((logout) -> logout
                                        .errorResponseHandler(failureHandler)
                                )
                        )
                        .authorizationEndpoint((endpoint) -> endpoint
                                .errorResponseHandler(failureHandler)
                        )
                        .tokenEndpoint((tokenEndpoint) -> tokenEndpoint
                                .errorResponseHandler(failureHandler)
                        )
                        .clientAuthentication((clientAuth) -> clientAuth
                                .errorResponseHandler(failureHandler)
                        )
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .authenticationEntryPoint(entryPoint)
                        .jwt(withDefaults()));
        return http.cors(withDefaults()).build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            AuthenticationFailureHandler failureHandler,
            AuthenticationProvider authenticationProvider,
            SessionRegistry sessionRegistry
    ) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login.html", "/login").access(this::hasValidOAuthSession)
                        .requestMatchers("/error", "/oauth2/authenticate").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .formLogin(configurer -> configurer
                        .loginPage("/login")
                        .loginProcessingUrl("/oauth2/authenticate")
                        .failureHandler(failureHandler)
                )
                .logout(logout -> logout
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new OAuth2LoginAuthenticationEntryPoint())
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.cors(withDefaults()).build();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .build();
    }

    private AuthorizationDecision hasValidOAuthSession(Supplier<Authentication> authentication,
                                                       RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return new AuthorizationDecision(false);
        }
        DefaultSavedRequest savedRequest = (DefaultSavedRequest)
                request.getSession().getAttribute(SessionAttributeNames.DEFAULT_SAVED_REQUEST_ATTR);

        if (Objects.isNull(savedRequest) ||
            Objects.isNull(savedRequest.getParameterMap().get(OAuth2ParameterNames.CLIENT_ID))) {
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(true);
    }
}
