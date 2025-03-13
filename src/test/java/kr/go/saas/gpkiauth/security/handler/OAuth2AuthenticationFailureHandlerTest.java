package kr.go.saas.gpkiauth.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.saas.gpkiauth.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OAuth2AuthenticationFailureHandlerTest {

    @Test
    void authenticationFailureHandlerTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        OAuth2AuthenticationFailureHandler failureHandler = new OAuth2AuthenticationFailureHandler();

        // when/then
        assertThrows(ApiException.class, () -> {
            failureHandler.onAuthenticationFailure(request, response, exception);
        });
    }
}