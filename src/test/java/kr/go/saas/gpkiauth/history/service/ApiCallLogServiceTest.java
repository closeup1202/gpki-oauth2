package kr.go.saas.gpkiauth.history.service;

import kr.go.saas.gpkiauth.history.entity.ApiCallLog;
import kr.go.saas.gpkiauth.history.model.ApiCallLogModel;
import kr.go.saas.gpkiauth.history.repository.ApiCallLogJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ApiCallLogServiceTest {

    @Autowired
    private ApiCallLogJpaRepository repository;

    @Test
    void save() {
        // given
        String traceId = "test-trace-id-abc-123";
        ApiCallLogModel model = ApiCallLogModel.builder()
                .path("testpath")
                .cnt(1L)
                .linkServiceId("testclient")
                .responseMessage("Success")
                .requestAt(Date.from(Instant.now()))
                .errorMessage(null)
                .traceId(traceId)
                .httpMethod("testHttpMethod")
                .statusCode("200 OK")
                .elapseHour(123L)
                .build();

        ApiCallLogService service = new ApiCallLogService(repository);

        // when
        service.save(model);
        Optional<ApiCallLog> fetchedLog = repository.findByTraceId(traceId);

        // then
        assertThat(fetchedLog).isPresent();
        assertThat(fetchedLog.get().getTraceId()).isEqualTo(traceId);
        assertThat(fetchedLog.get().getLinkServiceId()).isEqualTo("testclient");
        assertThat(fetchedLog.get().getResponseMessage()).isEqualTo("Success");
        assertThat(fetchedLog.get().getStatusCode()).isEqualTo("200 OK");
        assertThat(fetchedLog.get().getElapseHour()).isEqualTo(123L);
        assertThat(fetchedLog.get().getHttpMethod()).isEqualTo("testHttpMethod");
        assertThat(fetchedLog.get().getPath()).isEqualTo("testpath");
    }
}