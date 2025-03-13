package kr.go.saas.gpkiauth.common.exception;

import com.nimbusds.jose.shaded.gson.stream.MalformedJsonException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Map;
import java.util.Objects;

import static kr.go.saas.gpkiauth.common.exception.ErrorCodes.*;

public final class ExceptionMapper {

    private ExceptionMapper() {
    }

    public static final Map<Class<? extends Exception>, ErrorCodes> exceptionMap = Map.ofEntries(
            Map.entry(ServletException.class, GA10001),
            Map.entry(MalformedJsonException.class, GA10002),
            Map.entry(EntityNotFoundException.class, GA10003),
            Map.entry(ResourceNotFoundException.class, GA10003),
            Map.entry(NoSuchClientIdException.class, GA10004),
            Map.entry(HttpRequestMethodNotSupportedException.class, GA10005),
            Map.entry(AuthenticationException.class, GA20001),
            Map.entry(BadCredentialsException.class, GA20001),
            Map.entry(SecurityException.class, GA20002),
            Map.entry(AccessDeniedException.class, GA20002),
            Map.entry(IllegalArgumentException.class, GA20003),
            Map.entry(OAuth2AuthorizationCodeRequestAuthenticationException.class, GA20003),
            Map.entry(OAuth2AuthenticationException.class, GA20003),
            Map.entry(InsufficientAuthenticationException.class, GA20004),
            Map.entry(InvalidBearerTokenException.class, GA30002)
    );

    public static ErrorCodes getErrorCode(Exception e) {
        return Objects.requireNonNullElseGet(exceptionMap.get(e.getClass()),
                () -> exceptionMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().isAssignableFrom(e.getClass()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElse(GA10001)
        );
    }
}
