package kr.go.saas.gpkiauth.history.model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.saas.gpkiauth.common.exception.ApiException;
import kr.go.saas.gpkiauth.common.exception.ExceptionMapper;
import lombok.Builder;

import java.time.Instant;
import java.util.Date;

@Builder
public record ApiCallLogModel(
        String linkServiceId,
        String traceId,
        String httpMethod,
        Date requestAt,
        String path,
        Long elapseHour,
        Long cnt,
        String statusCode,
        String responseMessage,
        String errorMessage
) {
    private static ApiCallLogModel build(
            HttpServletRequest request,
            String clientId,
            long duration,
            String statusCode,
            String responseMessage,
            String errorMessage
    ) {
        return ApiCallLogModel.builder()
                .linkServiceId(clientId)
                .traceId(String.valueOf(request.getAttribute("traceId")))
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .requestAt(Date.from(Instant.now()))
                .elapseHour(duration)
                .cnt(1L)
                .statusCode(statusCode)
                .responseMessage(responseMessage)
                .errorMessage(errorMessage)
                .build();
    }

    public static ApiCallLogModel onSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            String clientId,
            long duration
    ) {
        return build(request, clientId, duration,
                String.valueOf(response.getStatus()), "Success", null);
    }

    public static ApiCallLogModel onFail(
            HttpServletRequest request,
            Exception e,
            String clientId,
            long duration
    ) {
        if (e instanceof ApiException ex) {
            return build(request, clientId, duration,
                    ex.getError().getStatus().toString(),
                    ex.getError().getMessage(),
                    ex.getErrorMessage());
        }

        return build(request, clientId, duration,
                ExceptionMapper.getErrorCode(e).getStatus().toString(),
                e.getMessage(),
                "[" + e.getClass().getSimpleName() + "] " + e.getMessage());
    }
}
