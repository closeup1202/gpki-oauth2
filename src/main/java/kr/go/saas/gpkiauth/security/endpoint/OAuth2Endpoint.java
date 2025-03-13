package kr.go.saas.gpkiauth.security.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OAuth2Endpoint {

    AUTHORIZE("/oauth2/authorize"),
    DEVICE_AUTHORIZATION("/oauth2/device_authorization"),
    DEVICE_VERIFICATION("/oauth2/device_verification"),
    TOKEN("/oauth2/token"),
    TOKEN_INTROSPECTION("/oauth2/introspect"),
    TOKEN_REVOCATION("/oauth2/revoke"),
    JWKS("/oauth2/jwks"),
    OIDC_LOGOUT("/connect/logout"),
    OIDC_USER_INFO("/userinfo"),
    OIDC_CLIENT_REGISTRATION("/connect/register"),
    LOGIN("/login"),
    LOGIN_AUTHENTICATE("/oauth2/authenticate");

    private final String path;

    public static boolean match(String requestURI) {
        return Arrays.stream(OAuth2Endpoint.values())
                .map(OAuth2Endpoint::getPath)
                .anyMatch(requestURI::contains);
    }
}
