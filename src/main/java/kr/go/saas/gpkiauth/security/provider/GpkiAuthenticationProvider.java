package kr.go.saas.gpkiauth.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class GpkiAuthenticationProvider implements AuthenticationProvider {

    private final HttpServletRequest request;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String resultParam = request.getParameter("result");
        boolean isValid = Boolean.parseBoolean(resultParam);
        if (!isValid) {
            throw new BadCredentialsException("Authentication failed - Invalid value");
        }

        String cn = request.getParameter("cn");
        String username = request.getParameter("username");
        String code = request.getParameter("code");

        GpkiPrincipal principal = GpkiPrincipal.builder()
                .name(username)
                .cn(cn)
                .instCode(code)
                .build();
        return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
