package kr.go.saas.gpkiauth.history.service;

import kr.go.saas.gpkiauth.history.entity.ApiCallLog;
import kr.go.saas.gpkiauth.history.model.ApiCallLogModel;
import kr.go.saas.gpkiauth.history.repository.ApiCallLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCallLogService {

    private final ApiCallLogJpaRepository repository;

    public void save(ApiCallLogModel model) {
        repository.save(ApiCallLog.from(model));
        log.info(printLog(model, true));
    }

    public void saveWithError(ApiCallLogModel model) {
        repository.save(ApiCallLog.from(model));
        log.error(printLog(model, false));
    }

    private String printLog(ApiCallLogModel model, boolean isSuccess) {
        String action = isSuccess ? "Successful API call" : "API call failed";
        return """
                {"serviceName":"GPLS", "action":"%s", "path":"%s", "requestTime":"%s", "httpMethod":"%s", "linkSrvcId":"%s", "duration":"%s", "status":"%s", "message":"%s"}
                """.formatted(
                action,
                model.path(),
                model.requestAt(),
                model.httpMethod(),
                model.linkServiceId(),
                model.elapseHour(),
                model.statusCode(),
                model.responseMessage()
        );
    }
}
