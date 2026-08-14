package app.unifeed.security;

import app.unifeed.error.BusinessException;
import app.unifeed.error.ErrorCode;
import org.springframework.http.HttpStatus;

public final class BusinessAuthenticationException extends BusinessException {
    public BusinessAuthenticationException() { super(ErrorCode.AUTH_009_INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED); }
}
