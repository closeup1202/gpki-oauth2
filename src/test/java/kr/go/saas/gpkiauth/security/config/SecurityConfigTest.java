package kr.go.saas.gpkiauth.security.config;

import kr.go.saas.gpkiauth.security.client.entity.TestRedisConfig;
import kr.go.saas.gpkiauth.security.client.repository.ClientJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisServerConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private ClientJpaRepository clientJpaRepository;

    @MockitoBean
    OAuth2TokenCustomizer<JwtEncodingContext> customizer;

    private static final String UUID = "test-uuid-id-123";
    private static final String CLIENT_ID = "test-client";
    private static final String CLIENT_SECRET = "test-secret";
    private static final String REDIRECT_URI = "http://localhost:5173/oauth2/callback";
    private static final String SCOPE = "openid";
    private static final String RESPONSE_TYPE = "code";
    private static final String STATE = "abc123";

    @BeforeEach
    void setUp() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        RegisteredClient client = RegisteredClient.withId(UUID)
                .clientId(CLIENT_ID)
                .clientSecret(encoder.encode(CLIENT_SECRET))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(REDIRECT_URI)
                .scope(SCOPE)
                .build();

        registeredClientRepository.save(client);
    }

    @AfterEach
    void tearDown() {
        clientJpaRepository.deleteById(UUID);
    }

    @Test
    void authorizationCodeFlow() throws Exception {
        MvcResult authorizationResult = mockMvc.perform(get("/oauth2/authorize")
                        .queryParam("response_type", RESPONSE_TYPE)
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("scope", SCOPE)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("state", STATE)
                        .with(user("user").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = authorizationResult.getResponse().getRedirectedUrl();
        String code = extractCodeFromUrl(redirectedUrl);

        assertThat(code).isNotBlank();
    }

    @Test
    void authorizationCodeFlowWithToken() throws Exception {
        MvcResult authorizationResult = mockMvc.perform(get("/oauth2/authorize")
                        .queryParam("response_type", RESPONSE_TYPE)
                        .queryParam("client_id", CLIENT_ID)
                        .queryParam("scope", SCOPE)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("state", STATE)
                        .with(user("user").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = authorizationResult.getResponse().getRedirectedUrl();
        String code = extractCodeFromUrl(redirectedUrl);

        mockMvc.perform(post("/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "authorization_code")
                        .param("client_id", CLIENT_ID)
                        .param("client_secret", CLIENT_SECRET)
                        .param("code", code)
                        .param("redirect_uri", REDIRECT_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.id_token").exists())
                .andReturn();
    }

    private String extractCodeFromUrl(String url) {
        return UriComponentsBuilder.fromUriString(url)
                .build()
                .getQueryParams()
                .getFirst("code");
    }
}