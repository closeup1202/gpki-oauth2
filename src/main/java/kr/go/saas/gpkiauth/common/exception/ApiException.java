package kr.go.saas.gpkiauth.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorCodes error;
    private final String errorMessage;

    public ApiException(Exception ex) {
        super(ex.getMessage());
        this.error = ExceptionMapper.getErrorCode(ex);
        this.errorMessage = setErrorMessage(ex);
    }

    private String setErrorMessage(Exception ex) {
        return "[" + ex.getClass().getSimpleName() + "] " + ex.getMessage();
    }
}
