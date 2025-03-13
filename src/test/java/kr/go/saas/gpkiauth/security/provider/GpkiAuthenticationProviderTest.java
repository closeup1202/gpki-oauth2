package kr.go.saas.gpkiauth.security.provider;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpkiAuthenticationProviderTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Authentication mockAuthentication;

    @Test
    void authenticationProvider() {
        // given
        GpkiAuthenticationProvider provider = new GpkiAuthenticationProvider(mockRequest);

        when(mockRequest.getParameter("result")).thenReturn("true");
        when(mockRequest.getParameter("cn")).thenReturn("300홍길동001");
        when(mockRequest.getParameter("dn")).thenReturn("과장");

        // when
        Authentication authenticate = provider.authenticate(mockAuthentication);

        // then
        assertNotNull(authenticate);
        assertTrue(authenticate.isAuthenticated());
        assertInstanceOf(GpkiPrincipal.class, authenticate.getPrincipal());

        GpkiPrincipal principal = (GpkiPrincipal) authenticate.getPrincipal();
        assertEquals("홍길동", principal.name());
        assertEquals("300홍길동001", principal.cn());
        assertEquals("과장", principal.dn());
        assertTrue(authenticate.getAuthorities().isEmpty());

        verify(mockRequest).getParameter("result");
        verify(mockRequest).getParameter("cn");
        verify(mockRequest).getParameter("dn");
    }

    @Test
    void authenticate_ThrowsException_WhenNullResultParameter() {
        // given
        when(mockRequest.getParameter("result")).thenReturn("false");

        GpkiAuthenticationProvider provider = new GpkiAuthenticationProvider(mockRequest);

        // when/then
        assertThrows(BadCredentialsException.class, () -> {
            provider.authenticate(mockAuthentication);
        });
    }

    @Test
    void supports_ReturnsTrue_ForUsernamePasswordAuthenticationToken() {
        // when
        GpkiAuthenticationProvider provider = new GpkiAuthenticationProvider(mockRequest);
        boolean result = provider.supports(UsernamePasswordAuthenticationToken.class);

        // then
        assertTrue(result);
    }

    @Test
    void supports_ReturnsFalse_ForOtherAuthenticationTypes() {
        // when
        GpkiAuthenticationProvider provider = new GpkiAuthenticationProvider(mockRequest);
        boolean result = provider.supports(TestAuthenticationToken.class);

        // then
        assertFalse(result);
    }

    // 테스트용 더미 Authentication 구현체
    private static class TestAuthenticationToken implements Authentication {
        @Override public String getName() { return null; }
        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
        @Override public Object getCredentials() { return null; }
        @Override public Object getDetails() { return null; }
        @Override public Object getPrincipal() { return null; }
        @Override public boolean isAuthenticated() { return false; }
        @Override public void setAuthenticated(boolean isAuthenticated) { }
    }
}