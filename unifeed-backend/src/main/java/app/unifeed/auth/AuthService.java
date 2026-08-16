package app.unifeed.auth;

import app.unifeed.error.BusinessException;
import app.unifeed.error.ErrorCode;
import app.unifeed.cache.OtpStore;
import app.unifeed.cache.PasswordResetStore;
import app.unifeed.mail.EmailOutboxService;
import app.unifeed.security.JwtService;
import app.unifeed.security.RefreshTokenService;
import app.unifeed.security.TestDomainAuthStrategy;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final JdbcTemplate jdbc; private final PasswordEncoder passwords; private final OtpStore otps;
    private final JwtService jwt; private final RefreshTokenService refresh; private final EmailOutboxService emailOutbox;
    private final TestDomainAuthStrategy testDomains; private final PasswordResetStore passwordResets; private final boolean allowTests;
    private final SecureRandom random = new SecureRandom();
    public AuthService(JdbcTemplate jdbc, PasswordEncoder passwords, OtpStore otps, JwtService jwt,
        RefreshTokenService refresh, EmailOutboxService emailOutbox, ObjectProvider<TestDomainAuthStrategy> tests, PasswordResetStore passwordResets,
        @Value("${app.security.allow-test-domains:false}") boolean allowTests) {
        this.jdbc=jdbc; this.passwords=passwords; this.otps=otps; this.jwt=jwt; this.refresh=refresh; this.emailOutbox=emailOutbox;
        this.testDomains=tests.getIfAvailable(); this.passwordResets=passwordResets; this.allowTests=allowTests;
    }
    @Transactional
    public UUID register(String email, String password, String displayName, Locale locale) {
        String normalized=email.trim().toLowerCase(Locale.ROOT); boolean test=isTest(normalized);
        if (!isAcademic(normalized) && !test) throw new BusinessException(ErrorCode.AUTH_001_DOMAIN_REJECTED,HttpStatus.FORBIDDEN);
        if(Boolean.TRUE.equals(jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM users WHERE email=? AND deleted_at IS NULL)",Boolean.class,normalized)))
            throw new BusinessException(ErrorCode.AUTH_014_EMAIL_ALREADY_REGISTERED,HttpStatus.CONFLICT);
        UUID id=UUID.randomUUID();
        jdbc.update("INSERT INTO users(id,email,display_name,password_hash,is_test_account) VALUES(?,?,?,?,?)",id,normalized,displayName,passwords.encode(password),test);
        String otp="%06d".formatted(random.nextInt(1_000_000)); otps.save(normalized, otp);
        emailOutbox.enqueue("email-events", id.toString(), new EmailEvent(UUID.randomUUID(),"USER_REGISTRATION",normalized,locale.getLanguage(),java.util.Map.of("username",displayName,"otp",otp)));
        return id;
    }
    public AuthTokens verifyOtp(String email,String otp) {
        String normalized=email.trim().toLowerCase(Locale.ROOT); String stored=otps.consume(normalized);
        if (stored==null || !stored.equals(otp)) throw new BusinessException(ErrorCode.AUTH_010_INVALID_OTP,HttpStatus.UNAUTHORIZED);
        jdbc.update("UPDATE users SET is_email_verified=true WHERE email=?",normalized);
        return issueForEmail(normalized);
    }
    public AuthTokens rotate(String token) { UUID id=refresh.ownerOf(token); var rotated=refresh.rotate(id,token); return userTokens(id,rotated); }
    public AuthTokens login(String email,String password) {
        String normalized=email.trim().toLowerCase(Locale.ROOT);
        LoginRow row=jdbc.query("SELECT id,password_hash,is_email_verified FROM users WHERE email=? AND deleted_at IS NULL",
            (rs,n)->new LoginRow((UUID)rs.getObject(1),rs.getString(2),rs.getBoolean(3)),normalized).stream().findFirst()
            .orElseThrow(()->new BusinessException(ErrorCode.AUTH_011_INVALID_CREDENTIALS,HttpStatus.UNAUTHORIZED));
        if(row.passwordHash()==null||!passwords.matches(password,row.passwordHash()))throw new BusinessException(ErrorCode.AUTH_011_INVALID_CREDENTIALS,HttpStatus.UNAUTHORIZED);
        if(!row.verified())throw new BusinessException(ErrorCode.AUTH_012_EMAIL_NOT_VERIFIED,HttpStatus.FORBIDDEN);
        return userTokens(row.id(),refresh.issue(row.id()));
    }
    public void requestPasswordReset(String email,Locale locale) {
        String normalized=email.trim().toLowerCase(Locale.ROOT);
        List<Map<String,Object>> users=jdbc.queryForList("SELECT id,display_name FROM users WHERE email=? AND password_hash IS NOT NULL AND deleted_at IS NULL",normalized);
        if(users.isEmpty())return;
        String otp="%06d".formatted(random.nextInt(1_000_000)); passwordResets.save(normalized,otp);
        Map<String,Object> user=users.getFirst();
        emailOutbox.enqueue("email-events",user.get("id").toString(),new EmailEvent(UUID.randomUUID(),"PASSWORD_RESET",normalized,locale.getLanguage(),Map.of("username",user.get("display_name").toString(),"otp",otp)));
    }
    public void resetPassword(String email,String token,String newPassword) {
        String normalized=email.trim().toLowerCase(Locale.ROOT); String stored=passwordResets.consume(normalized);
        if(stored==null||!stored.equals(token))throw new BusinessException(ErrorCode.AUTH_013_INVALID_RESET_TOKEN,HttpStatus.UNAUTHORIZED);
        if(jdbc.update("UPDATE users SET password_hash=? WHERE email=? AND deleted_at IS NULL",passwords.encode(newPassword),normalized)==0)
            throw new BusinessException(ErrorCode.COMMON_404_NOT_FOUND,HttpStatus.NOT_FOUND);
    }
    public void logout(UUID userId){refresh.revoke(userId);}
    private AuthTokens issueForEmail(String email) { return jdbc.queryForObject("SELECT id,is_test_account FROM users WHERE email=? AND deleted_at IS NULL",(rs,n)->userTokens((UUID)rs.getObject(1),refresh.issue((UUID)rs.getObject(1))),email); }
    private AuthTokens userTokens(UUID id, RefreshTokenService.IssuedRefreshToken issued) {
        return jdbc.queryForObject("SELECT email,global_role::text,is_test_account FROM users WHERE id=?",(rs,n)->new AuthTokens(jwt.issueAccessToken(id,rs.getString(1),rs.getString(2)),issued.cookie().toString(),rs.getBoolean(3)),id);
    }
    private boolean isAcademic(String email){return email.endsWith(".edu")||email.endsWith(".edu.tr");}
    private boolean isTest(String email){return testDomains!=null&&testDomains.accepts(email,allowTests);}
    public record AuthTokens(String accessToken,@com.fasterxml.jackson.annotation.JsonIgnore String refreshCookie,boolean testAccount){}
    private record LoginRow(UUID id,String passwordHash,boolean verified){}
    public record EmailEvent(UUID eventId,String eventType,String recipientEmail,String language,java.util.Map<String,String> templateData){}
}
