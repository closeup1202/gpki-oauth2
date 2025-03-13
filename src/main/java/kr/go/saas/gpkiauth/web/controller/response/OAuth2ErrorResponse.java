package kr.go.saas.gpkiauth.web.controller.response;

import kr.go.saas.gpkiauth.common.exception.ErrorCodes;
import lombok.Getter;

@Getter
public class OAuth2ErrorResponse {

    private final String code;
    private final String message;
    private final String status;
    private final String traceId;

    public OAuth2ErrorResponse(ErrorCodes errorCodes, String traceId) {
        this.code = errorCodes.name();
        this.message = errorCodes.getMessage();
        this.status = errorCodes.getStatus().toString();
        this.traceId = traceId;
    }
}
