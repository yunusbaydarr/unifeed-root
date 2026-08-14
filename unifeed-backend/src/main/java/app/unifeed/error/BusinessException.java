package app.unifeed.error;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus status;
    public BusinessException(ErrorCode errorCode, HttpStatus status) {
        super(errorCode.name()); this.errorCode = errorCode; this.status = status;
    }
    public ErrorCode errorCode() { return errorCode; }
    public HttpStatus status() { return status; }
}
