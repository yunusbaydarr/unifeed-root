package app.unifeed.error;

public enum ErrorCode {
    AUTH_001_DOMAIN_REJECTED("auth.domain.rejected"),
    AUTH_008_FORBIDDEN_CLUB_ACTION("club.access.denied"),
    AUTH_009_INVALID_REFRESH_TOKEN("auth.refresh.invalid"),
    AUTH_010_INVALID_OTP("auth.otp.invalid"),
    AUTH_011_INVALID_CREDENTIALS("auth.credentials.invalid"),
    AUTH_012_EMAIL_NOT_VERIFIED("auth.email.not-verified"),
    AUTH_013_INVALID_RESET_TOKEN("auth.reset.invalid"),
    AUTH_014_EMAIL_ALREADY_REGISTERED("auth.email.already-registered"),
    COMMON_403_FORBIDDEN("common.forbidden"),
    COMMON_404_NOT_FOUND("common.not-found"),
    COMMON_409_CONFLICT("common.conflict"),
    COMMON_001_VALIDATION_FAILED("common.validation.failed"),
    COMMON_500_INTERNAL_ERROR("common.internal.error");

    private final String messageKey;
    ErrorCode(String messageKey) { this.messageKey = messageKey; }
    public String messageKey() { return messageKey; }
}
