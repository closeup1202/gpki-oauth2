package kr.go.saas.gpkiauth.security.client.repository;

import kr.go.saas.gpkiauth.security.client.entity.Authorization;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthorizationRepository extends CrudRepository<Authorization, String> {
	Optional<Authorization> findByState(String state);
	Optional<Authorization> findByAuthorizationCodeValue(String authorizationCode);
	Optional<Authorization> findByAccessTokenValue(String accessToken);
	Optional<Authorization> findByRefreshTokenValue(String refreshToken);
	Optional<Authorization> findByOidcIdTokenValue(String idToken);
}
