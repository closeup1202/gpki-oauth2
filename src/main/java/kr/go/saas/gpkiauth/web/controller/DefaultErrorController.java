package kr.go.saas.gpkiauth.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.go.saas.gpkiauth.common.exception.ApiException;
import kr.go.saas.gpkiauth.common.exception.ErrorCodes;
import kr.go.saas.gpkiauth.common.exception.ExceptionMapper;
import kr.go.saas.gpkiauth.web.controller.response.OAuth2ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DefaultErrorController implements ErrorController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping("/error")
    public OAuth2ErrorResponse handleError(HttpServletRequest request) {
        Object errorAttribute = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String traceId = request.getAttribute("traceId").toString();
        String clientId = request.getAttribute("clientId").toString();
        if (errorAttribute instanceof ApiException ex) {
            return new OAuth2ErrorResponse(ex.getError(), traceId);
        }
        Exception ex = (Exception) errorAttribute;
        if (ex != null) {
            return new OAuth2ErrorResponse(ExceptionMapper.getErrorCode(ex), traceId);
        }
        if (clientId.equals("unknown")) {
            return new OAuth2ErrorResponse(ErrorCodes.GA20004, traceId);
        }
        return new OAuth2ErrorResponse(ErrorCodes.GA10002, traceId);
    }
}
