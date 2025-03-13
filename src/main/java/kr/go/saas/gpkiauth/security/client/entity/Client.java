package kr.go.saas.gpkiauth.security.client.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@ToString
@Getter
@Setter
@Table(name = "tb_gpls_client")
public class Client {
    @Id
    @Column(name = "uuid")
    private String id = UUID.randomUUID().toString();

    private String clientId;

    @Column(name = "client_id_issu_de")
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret_key")
    private String clientSecret;

    @Column(name = "client_secret_key_expry_de")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_nm")
    private String clientName;

    @Column(name = "client_authrt_mthd", length = 1000)
    private String clientAuthenticationMethods;

    @Column(name = "authrt_grant_ty", length = 1000)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uri", length = 1000)
    private String redirectUris;

    @Column(name = "lgt_aftr_redirect_uri", length = 1000)
    private String postLogoutRedirectUris;

    @Column(name = "scope", length = 1000)
    private String scopes = "openid";

    @Column(name = "client_stng", length = 2000)
    private String clientSettings;

    @Column(name = "token_stng", length = 2000)
    private String tokenSettings;

    @Column(name = "api_key_stts_cd")
    private String apiKeyStatusCode;

    private String testKeyYn;

}
