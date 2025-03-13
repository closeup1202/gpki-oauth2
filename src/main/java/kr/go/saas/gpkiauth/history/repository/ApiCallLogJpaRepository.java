package kr.go.saas.gpkiauth.history.repository;

import kr.go.saas.gpkiauth.history.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiCallLogJpaRepository extends JpaRepository<ApiCallLog, Long> {
    Optional<ApiCallLog> findByTraceId(String traceId);
}
