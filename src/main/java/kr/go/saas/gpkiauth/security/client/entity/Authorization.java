package kr.go.saas.gpkiauth.security.client.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@RedisHash(value = "auth:oauth2:authorization", timeToLive = 43200)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Authorization {
	@Id
	@Column
	private String id;
	private String registeredClientId;
	private String principalName;
	private String authorizationGrantType;
	@Column(length = 1000)
	private String authorizedScopes;
	@Column(length = 4000)
	private String attributes;
	@Indexed
	@Column(length = 500)
	private String state;

	@Indexed
	@Column(length = 4000)
	private String authorizationCodeValue;
	private Instant authorizationCodeIssuedAt;
	private Instant authorizationCodeExpiresAt;
	private String authorizationCodeMetadata;

	@Indexed
	@Column(length = 4000)
	private String accessTokenValue;
	private Instant accessTokenIssuedAt;
	private Instant accessTokenExpiresAt;
	@Column(length = 2000)
	private String accessTokenMetadata;
	private String accessTokenType;
	@Column(length = 1000)
	private String accessTokenScopes;

	@Indexed
	@Column(length = 4000)
	private String refreshTokenValue;
	private Instant refreshTokenIssuedAt;
	private Instant refreshTokenExpiresAt;
	@Column(length = 2000)
	private String refreshTokenMetadata;

	@Indexed
	@Column(length = 4000)
	private String oidcIdTokenValue;
	private Instant oidcIdTokenIssuedAt;
	private Instant oidcIdTokenExpiresAt;
	@Column(length = 2000)
	private String oidcIdTokenMetadata;
	@Column(length = 2000)
	private String oidcIdTokenClaims;
}
