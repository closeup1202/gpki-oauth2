package kr.go.saas.gpkiauth.security.client.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.go.saas.gpkiauth.security.endpoint.SessionAttributeNames;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientIdService {

    private final OAuth2AuthorizationService service;

    public static final String UNKNOWN_CLIENT_ID = "unknown";

    public String extract(HttpServletRequest request) {
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        if (StringUtils.hasText(clientId)) {
            return clientId;
        }

        String clientIdByToken = getFromTokens(request);
        if (StringUtils.hasText(clientIdByToken)) {
            return clientIdByToken;
        }
        return getFromSession(request.getSession(false));
    }

    private String getFromTokens(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token)) {
            String accessToken = token.replace("Bearer ", "");
            return getRegisteredClientId(accessToken, OAuth2TokenType.ACCESS_TOKEN);
        }

        String idToken = request.getParameter(OidcParameterNames.ID_TOKEN + "_hint");
        if (StringUtils.hasText(idToken)) {
            return getRegisteredClientId(idToken, new OAuth2TokenType(OidcParameterNames.ID_TOKEN));
        }
        return null;
    }

    private String getRegisteredClientId(String token, OAuth2TokenType tokenType) {
        OAuth2Authorization authorization = service.findByToken(token, tokenType);
        return Objects.nonNull(authorization) ? authorization.getRegisteredClientId() : null;
    }

    private String getFromSession(HttpSession session) {
        if (session == null) {
            return UNKNOWN_CLIENT_ID;
        }

        DefaultSavedRequest savedRequest = (DefaultSavedRequest)
                session.getAttribute(SessionAttributeNames.DEFAULT_SAVED_REQUEST_ATTR);

        if (savedRequest == null ||
            savedRequest.getParameterMap().get(OAuth2ParameterNames.CLIENT_ID) == null) {
            return UNKNOWN_CLIENT_ID;
        }

        return savedRequest.getParameterMap().get(OAuth2ParameterNames.CLIENT_ID)[0];
    }
}
