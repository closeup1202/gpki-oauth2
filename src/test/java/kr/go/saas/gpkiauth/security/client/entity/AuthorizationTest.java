package kr.go.saas.gpkiauth.security.client.entity;

import kr.go.saas.gpkiauth.security.client.repository.AuthorizationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestRedisConfig.class)
class AuthorizationTest {

    @Autowired
    private RedisTemplate<String, Authorization> redisTemplate;

    @Autowired
    AuthorizationRepository authorizationRepository;

    private Authorization authorization;

    @BeforeEach
    void setUp() {
        authorization = new Authorization();
        authorization.setId("test-auth-id");
        authorization.setRegisteredClientId("test-client-id");
        authorization.setPrincipalName("user1");
        authorization.setAuthorizationGrantType("authorization_code");
        authorization.setAuthorizedScopes("openid");
        authorization.setState("state123");
        authorization.setAccessTokenValue("access-token-123");
        authorization.setAccessTokenIssuedAt(Instant.now());
        authorization.setAccessTokenExpiresAt(Instant.now().plusSeconds(3600));
    }

    @Test
    void saveAndFindAuthorization() {
        // given
        String key = "auth:oauth2:authorization:" + authorization.getId();

        // when
        redisTemplate.opsForValue().set(key, authorization);
        Authorization foundAuthorization = redisTemplate.opsForValue().get(key);

        // then
        assertThat(foundAuthorization).isNotNull();
        assertThat(foundAuthorization.getId()).isEqualTo(authorization.getId());
        assertThat(foundAuthorization.getRegisteredClientId()).isEqualTo(authorization.getRegisteredClientId());
        assertThat(foundAuthorization.getPrincipalName()).isEqualTo(authorization.getPrincipalName());
        assertThat(foundAuthorization.getAccessTokenValue()).isEqualTo(authorization.getAccessTokenValue());
    }

    @Test
    void checkTimeToLive() {
        // given
        String key = "auth:oauth2:authorization:" + authorization.getId();
        redisTemplate.opsForValue().set(key, authorization);

        // when
        Long ttl = redisTemplate.getExpire(key);

        // then
        assertThat(ttl).isNotNull();
        assertThat(ttl).isLessThanOrEqualTo(43200L);
    }

    @Test
    void findByIndex() {
        // given
        Authorization savedAuthorization = authorizationRepository.save(authorization);

        // when
        Optional<Authorization> fetchedAuthorization = authorizationRepository.findByAccessTokenValue(authorization.getAccessTokenValue());

        // then
        assertThat(fetchedAuthorization.isPresent()).isTrue();
        assertThat(fetchedAuthorization.get().getId()).isEqualTo(savedAuthorization.getId());
        assertThat(fetchedAuthorization.get().getAccessTokenValue()).isEqualTo(savedAuthorization.getAccessTokenValue());
        assertThat(fetchedAuthorization.get().getAccessTokenExpiresAt()).isEqualTo(savedAuthorization.getAccessTokenExpiresAt());
        assertThat(fetchedAuthorization.get().getAccessTokenType()).isEqualTo(savedAuthorization.getAccessTokenType());
        assertThat(fetchedAuthorization.get().getAuthorizationCodeValue()).isEqualTo(savedAuthorization.getAuthorizationCodeValue());
        assertThat(fetchedAuthorization.get().getOidcIdTokenValue()).isEqualTo(savedAuthorization.getOidcIdTokenValue());
        assertThat(fetchedAuthorization.get().getAuthorizedScopes()).isEqualTo(savedAuthorization.getAuthorizedScopes());
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete("auth:oauth2:authorization:" + authorization.getId());
    }
}