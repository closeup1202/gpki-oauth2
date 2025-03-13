package kr.go.saas.gpkiauth.security.entrypoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.saas.gpkiauth.common.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OAuth2AuthenticationEntryPointTest {

    @Test
    void authenticationEntryPointTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();

        // when/then
        assertThrows(ApiException.class, () -> {
            entryPoint.commence(request, response, exception);
        });
    }

}