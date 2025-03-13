package kr.go.saas.gpkiauth.security.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserMapperConfigTest {

    @Test
    void testUserInfoMapper() {
        // given
        UserMapperConfig userMapperConfig = new UserMapperConfig();
        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = userMapperConfig.userInfoMapper();

        Map<String, Object> tokenAttributes = new HashMap<>();
        tokenAttributes.put("name", "홍길동");
        tokenAttributes.put("cn", "행정안전부");
        tokenAttributes.put("dn", "팀장");

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(tokenAttributes);

        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

        OidcUserInfoAuthenticationToken oidcAuthenticationToken = mock(OidcUserInfoAuthenticationToken.class);
        when(oidcAuthenticationToken.getPrincipal()).thenReturn(jwtAuthenticationToken);

        OidcUserInfoAuthenticationContext context = mock(OidcUserInfoAuthenticationContext.class);
        when(context.getAuthentication()).thenReturn(oidcAuthenticationToken);

        // when
        OidcUserInfo userInfo = userInfoMapper.apply(context);

        // then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getClaims()).containsEntry("name", "홍길동");
        assertThat(userInfo.getClaims()).containsEntry("cn", "행정안전부");
        assertThat(userInfo.getClaims()).containsEntry("dn", "팀장");
    }

}