package kr.go.saas.gpkiauth.common.exception;

public class NoSuchClientIdException extends RuntimeException {

    public NoSuchClientIdException(String clientId) {
        super(clientId + " dose not exist");
    }
}
