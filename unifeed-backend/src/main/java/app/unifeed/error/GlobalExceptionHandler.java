package app.unifeed.error;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messages;
    public GlobalExceptionHandler(MessageSource messages) { this.messages = messages; }

    @ExceptionHandler(BusinessException.class)
    ProblemDetail business(BusinessException ex, HttpServletRequest request, Locale locale) {
        log.warn("Business request rejected: code={}", ex.errorCode());
        return problem(ex.status(), ex.errorCode(), request, locale, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException ex, HttpServletRequest request, Locale locale) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage() == null ? "" : e.getDefaultMessage())).toList();
        return problem(HttpStatus.BAD_REQUEST, ErrorCode.COMMON_001_VALIDATION_FAILED, request, locale, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail malformedJson(HttpMessageNotReadableException ex, HttpServletRequest request, Locale locale) {
        return problem(HttpStatus.BAD_REQUEST, ErrorCode.COMMON_001_VALIDATION_FAILED, request, locale, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflict(DataIntegrityViolationException ex, HttpServletRequest request, Locale locale) {
        log.warn("Data integrity conflict at {}", request.getRequestURI());
        return problem(HttpStatus.CONFLICT, ErrorCode.COMMON_409_CONFLICT, request, locale, null);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception ex, HttpServletRequest request, Locale locale) {
        log.error("Unhandled request failure", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.COMMON_500_INTERNAL_ERROR, request, locale, null);
    }

    private ProblemDetail problem(HttpStatus status, ErrorCode code, HttpServletRequest request, Locale locale, Object validationErrors) {
        String detail = messages.getMessage(code.messageKey(), null, locale);
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setType(URI.create("https://unifeed.app/errors/" + code.name().toLowerCase().replace('_', '-')));
        p.setTitle(status.getReasonPhrase()); p.setInstance(URI.create(request.getRequestURI()));
        p.setProperty("code", code.name()); p.setProperty("timestamp", Instant.now()); p.setProperty("validationErrors", validationErrors);
        return p;
    }
}
