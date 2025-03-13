package kr.go.saas.gpkiauth.security.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.go.saas.gpkiauth.security.client.entity.Client;
import kr.go.saas.gpkiauth.security.client.repository.ClientJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ClientHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Value("${gpki.auth.oauth2.refresh-token-timeout}")
    private String refreshTokenTimeout;

    @Value("${gpki.auth.oauth2.access-token-timeout}")
    private String accessTokenTimeout;

    public ClientHelper() {
        ClassLoader classLoader = ClientHelper.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    public Client toEntity(RegisteredClient client) {
        List<String> methods = client.getClientAuthenticationMethods()
                .stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toList());

        List<String> types = client.getAuthorizationGrantTypes()
                .stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toList());

        Client entity = new Client();
        entity.setId(client.getId());
        entity.setClientId(client.getClientId());
        entity.setClientIdIssuedAt(client.getClientIdIssuedAt());
        entity.setClientSecret(client.getClientSecret());
        entity.setClientSecretExpiresAt(client.getClientSecretExpiresAt());
        entity.setClientName(client.getClientName());
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(methods));
        entity.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(types));
        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(client.getRedirectUris()));
        entity.setPostLogoutRedirectUris(StringUtils.collectionToCommaDelimitedString(client.getPostLogoutRedirectUris()));
        entity.setScopes(StringUtils.collectionToCommaDelimitedString(client.getScopes()));
        entity.setClientSettings(writeMap(client.getClientSettings().getSettings()));
        entity.setTokenSettings(writeMap(client.getTokenSettings().getSettings()));
        return entity;
    }

    public RegisteredClient toClient(Client client, ClientJpaRepository clientJpaRepository) {
        if (needsPasswordEncoding(client.getClientSecret())) {
            setInitialSettings(client);
            clientJpaRepository.save(client);
        }
        return toObject(client);
    }

    private boolean needsPasswordEncoding(String clientSecret) {
        return !clientSecret.startsWith("{bcrypt}");
    }

    private void setInitialSettings(Client client) {
        client.setClientSecret(encoder.encode(client.getClientSecret()));
        client.setClientSettings(writeMap(ClientSettings.builder()
                .requireAuthorizationConsent(true)
                .build().getSettings()));
        client.setTokenSettings(writeMap(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofSeconds(Long.parseLong(accessTokenTimeout)))
                .refreshTokenTimeToLive(Duration.ofSeconds(Long.parseLong(refreshTokenTimeout)))
                .reuseRefreshTokens(false)
                .build().getSettings()));
        client.setScopes(OidcScopes.OPENID);
        client.setClientAuthenticationMethods(getAuthenticationMethods());
        client.setAuthorizationGrantTypes(getAuthorizationGrantTypes());
    }

    private String getAuthenticationMethods() {
        return String.join(",", List.of(
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue(),
                ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue()
        ));
    }

    private String getAuthorizationGrantTypes() {
        return String.join(",", List.of(
                AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                AuthorizationGrantType.REFRESH_TOKEN.getValue(),
                AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()
        ));
    }

    private RegisteredClient toObject(Client client) {
        Map<String, Object> clientSettingsMap = parseMap(client.getClientSettings());
        Map<String, Object> tokenSettingsMap = parseMap(client.getTokenSettings());

        return RegisteredClient.withId(client.getClientId())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientAuthenticationMethods(addElements(client.getClientAuthenticationMethods(), ClientAuthenticationMethod::new))
                .authorizationGrantTypes(addElements(client.getAuthorizationGrantTypes(), AuthorizationGrantType::new))
                .redirectUris(addString(client.getRedirectUris()))
                .postLogoutRedirectUris(addString(client.getPostLogoutRedirectUris()))
                .scopes(addString(client.getScopes()))
                .clientSettings(ClientSettings.withSettings(clientSettingsMap).build())
                .tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build())
                .build();
    }

    private <T> Consumer<Set<T>> addElements(String values, Function<String, T> constructor) {
        return elements -> {
            Set<T> collect = StringUtils.commaDelimitedListToSet(values)
                    .stream()
                    .map(constructor)
                    .collect(Collectors.toUnmodifiableSet());
            elements.addAll(collect);
        };
    }

    private Consumer<Set<String>> addString(String values) {
        return s -> s.addAll(StringUtils.commaDelimitedListToSet(values));
    }

    private Map<String, Object> parseMap(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
