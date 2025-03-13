package kr.go.saas.gpkiauth.security.token.service;

import kr.go.saas.gpkiauth.security.provider.GpkiPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenCustomizerService implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            context.getClaims().claims((claims) -> {
                Authentication authentication = context.getPrincipal();
                Object principal = authentication.getPrincipal();
                if (principal instanceof GpkiPrincipal gpkiPrincipal) {
                    claims.put("name", gpkiPrincipal.name());
                    claims.put("cn", gpkiPrincipal.cn());
                    claims.put("instCode", gpkiPrincipal.instCode());
                } else {
                    throw new IllegalArgumentException("principal is not of type GpkiPrincipal");
                }
            });
        }
    }
}
