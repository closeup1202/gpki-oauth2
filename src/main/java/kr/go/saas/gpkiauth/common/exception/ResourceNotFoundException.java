package kr.go.saas.gpkiauth.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("요청하신 데이터를 찾을 수 없습니다");
    }
}
