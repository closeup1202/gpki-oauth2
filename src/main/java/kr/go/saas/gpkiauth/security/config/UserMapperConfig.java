package kr.go.saas.gpkiauth.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class UserMapperConfig {

    @Bean
    public Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper() {
        return (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication.getPrincipal();
            Map<String, Object> tokenAttributes = principal.getTokenAttributes();
            Map<String, Object> userInfo = new LinkedHashMap<>();
            userInfo.put("name", tokenAttributes.get("name"));
            userInfo.put("cn", tokenAttributes.get("cn"));
            userInfo.put("instCode", tokenAttributes.get("instCode"));
            return new OidcUserInfo(userInfo);
        };
    }
}
