package kr.go.saas.gpkiauth.security.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

@JsonDeserialize(builder = GpkiPrincipal.GpkiPrincipalBuilder.class)
@Builder
public record GpkiPrincipal(
        @JsonProperty("name") String name,
        @JsonProperty("cn") String cn,
        @JsonProperty("instCode") String instCode) {
}
