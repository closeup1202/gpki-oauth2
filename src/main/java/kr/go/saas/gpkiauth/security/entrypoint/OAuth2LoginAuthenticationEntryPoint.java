package kr.go.saas.gpkiauth.security.entrypoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.saas.gpkiauth.common.exception.ApiException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.AuthenticationEntryPoint;

public class OAuth2LoginAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException exception) {
            if (request.getSession(false) == null ||
                request.getSession(false).getAttribute(OAuth2ParameterNames.STATE) == null) {
                    throw new ApiException(exception);
            }
    }
}
