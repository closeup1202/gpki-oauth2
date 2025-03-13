package kr.go.saas.gpkiauth.security.client.repository;

import kr.go.saas.gpkiauth.security.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientJpaRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}
