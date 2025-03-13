package kr.go.saas.gpkiauth.security.token.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TokenConfig.class)
class TokenConfigTest {

    @Autowired
    private JWKSource<SecurityContext> jwkSource;

    @Test
    void jwkSourceBeanShouldBeCreated() {
        assertNotNull(jwkSource, "JWKSource 빈이 생성되지 않았습니다.");
    }

    @Test
    void jwkSourceShouldContainRsaKey() throws KeySourceException {
        JWKSelector jwkSelector = new JWKSelector(new com.nimbusds.jose.jwk.JWKMatcher.Builder().build());
        List<JWK> keys = jwkSource.get(jwkSelector, null);

        assertNotNull(keys, "JWKSource에서 가져온 키 목록이 null입니다.");
        assertFalse(keys.isEmpty(), "JWKSource에서 가져온 키 목록이 비어 있습니다.");

        JWK jwk = keys.get(0);
        assertInstanceOf(RSAKey.class, jwk, "첫 번째 키가 RSAKey 타입이 아닙니다.");

        RSAKey rsaKey = (RSAKey) jwk;
        assertNotNull(rsaKey.getKeyID(), "RSA 키의 ID가 null입니다.");
        assertNotNull(rsaKey.getModulus(), "RSA 키의 모듈러스가 null입니다.");
        assertNotNull(rsaKey.getPublicExponent(), "RSA 키의 공개 지수가 null입니다.");
    }

    @Test
    void rsaKeyShouldHaveValidKeyPair() throws JOSEException {
        JWKSelector jwkSelector = new JWKSelector(new com.nimbusds.jose.jwk.JWKMatcher.Builder().build());
        List<JWK> keys = jwkSource.get(jwkSelector, null);
        RSAKey rsaKey = (RSAKey) keys.get(0);

        assertTrue(rsaKey.isPrivate(), "RSA 키에 비공개 키가 없습니다.");
        assertDoesNotThrow(rsaKey::toKeyPair, "RSA 키 쌍이 유효하지 않습니다.");
        assertEquals(2048, rsaKey.toRSAPublicKey().getModulus().bitLength(),
                "RSA 키의 길이가 2048비트가 아닙니다.");
    }

    @Test
    void multipleCallsShouldReturnSameJwkSet() throws KeySourceException {
        JWKSelector jwkSelector = new JWKSelector(new com.nimbusds.jose.jwk.JWKMatcher.Builder().build());
        List<JWK> keys1 = jwkSource.get(jwkSelector, null);
        List<JWK> keys2 = jwkSource.get(jwkSelector, null);

        assertNotNull(keys1);
        assertNotNull(keys2);
        assertEquals(keys1.size(), keys2.size(), "두 번의 호출에서 반환된 키 개수가 다릅니다.");

        JWK jwk1 = keys1.get(0);
        JWK jwk2 = keys2.get(0);

        assertEquals(jwk1.getKeyID(), jwk2.getKeyID(), "두 번의 호출에서 반환된 키 ID가 다릅니다.");
        assertEquals(
                ((RSAKey) jwk1).getModulus(),
                ((RSAKey) jwk2).getModulus(),
                "두 번의 호출에서 반환된 키의 모듈러스가 다릅니다."
        );
    }
}