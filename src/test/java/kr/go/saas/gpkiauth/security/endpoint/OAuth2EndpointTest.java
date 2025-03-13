package kr.go.saas.gpkiauth.security.endpoint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2EndpointTest {

    @Test
    void matchFalse() {
        // given
        String requestUri = "/test";

        // when
        boolean match = OAuth2Endpoint.match(requestUri);

        // then
        assertFalse(match);
    }

    @Test
    void matchTrue() {
        // given
        String requestUri = "/connect/logout";

        // when
        boolean match = OAuth2Endpoint.match(requestUri);

        // then
        assertTrue(match);
    }

}