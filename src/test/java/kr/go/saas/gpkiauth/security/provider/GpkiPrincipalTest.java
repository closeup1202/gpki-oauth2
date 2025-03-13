package kr.go.saas.gpkiauth.security.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GpkiPrincipalTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void builder() {
        // Given & When
        GpkiPrincipal principal = GpkiPrincipal.builder()
                .name("홍길동")
                .cn("정보시스템부")
                .dn("과장")
                .build();

        // Then
        assertEquals("홍길동", principal.name());
        assertEquals("정보시스템부", principal.cn());
        assertEquals("과장", principal.dn());
    }

    @Test
    void null_values() {
        // Given & When
        GpkiPrincipal principal = GpkiPrincipal.builder()
                .name(null)
                .cn(null)
                .dn(null)
                .build();

        // Then
        assertNull(principal.name());
        assertNull(principal.cn());
        assertNull(principal.dn());
    }

    @Test
    void json_serialization() throws JsonProcessingException {
        // Given
        GpkiPrincipal principal = GpkiPrincipal.builder()
                .name("홍길동")
                .cn("정보시스템부")
                .dn("과장")
                .build();

        // When
        String json = objectMapper.writeValueAsString(principal);

        // Then
        assertTrue(json.contains("\"name\":\"홍길동\""));
        assertTrue(json.contains("\"cn\":\"정보시스템부\""));
        assertTrue(json.contains("\"dn\":\"과장\""));
    }

    @Test
    void json_deserialization() throws JsonProcessingException {
        // Given
        String json = """
                {
                    "name": "홍길동",
                    "cn": "정보시스템부",
                    "dn": "과장"
                }
                """;

        // When
        GpkiPrincipal principal = objectMapper.readValue(json, GpkiPrincipal.class);

        // Then
        assertEquals("홍길동", principal.name());
        assertEquals("정보시스템부", principal.cn());
        assertEquals("과장", principal.dn());
    }
}