package kr.go.saas.gpkiauth.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    GA10001("시스템 장애로 통신이 원활하지 않습니다", INTERNAL_SERVER_ERROR),
    GA10002("잘못된 요청입니다", BAD_REQUEST),
    GA10003("요청하신 데이터가 없습니다", NOT_FOUND),
    GA10004("등록된 Client ID가 없습니다", NOT_FOUND),
    GA10005("지원하지 않는 형식입니다", METHOD_NOT_ALLOWED),
    GA20001("인증되지 않았습니다", UNAUTHORIZED),
    GA20002("접근이 권한이 없습니다", FORBIDDEN),
    GA20003("요청에 필요한 항목이나 값이 잘못되었습니다", BAD_REQUEST),
    GA20004("OAuth2 인증 흐름을 통한 접근이 아닙니다", UNAUTHORIZED),
    GA30001("잘못된 토큰 형식입니다", BAD_REQUEST),
    GA30002("유효하지 않은 토큰입니다", UNAUTHORIZED),
    GA30003("유효하지 않은 API키입니다", UNAUTHORIZED);

    private final String message;
    private final HttpStatus status;
}
