package app.unifeed.auth;

import app.unifeed.error.BusinessException;
import app.unifeed.error.ErrorCode;
import app.unifeed.cache.OtpStore;
import app.unifeed.security.JwtService;
import app.unifeed.security.RefreshTokenService;
import app.unifeed.security.TestDomainAuthStrategy;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final JdbcTemplate jdbc; private final PasswordEncoder passwords; private final OtpStore otps;
    private final JwtService jwt; private final RefreshTokenService refresh; private final KafkaTemplate<String, Object> kafka;
    private final TestDomainAuthStrategy testDomains; private final boolean allowTests;
    private final SecureRandom random = new SecureRandom();
    public AuthService(JdbcTemplate jdbc, PasswordEncoder passwords, OtpStore otps, JwtService jwt,
        RefreshTokenService refresh, KafkaTemplate<String,Object> kafka, ObjectProvider<TestDomainAuthStrategy> tests,
        @Value("${app.security.allow-test-domains:false}") boolean allowTests) {
        this.jdbc=jdbc; this.passwords=passwords; this.otps=otps; this.jwt=jwt; this.refresh=refresh; this.kafka=kafka;
        this.testDomains=tests.getIfAvailable(); this.allowTests=allowTests;
    }
    @Transactional
    public UUID register(String email, String password, String displayName, Locale locale) {
        String normalized=email.trim().toLowerCase(Locale.ROOT); boolean test=isTest(normalized);
        if (!isAcademic(normalized) && !test) throw new BusinessException(ErrorCode.AUTH_001_DOMAIN_REJECTED,HttpStatus.FORBIDDEN);
        UUID id=UUID.randomUUID();
        jdbc.update("INSERT INTO users(id,email,display_name,password_hash,is_test_account) VALUES(?,?,?,?,?)",id,normalized,displayName,passwords.encode(password),test);
        String otp="%06d".formatted(random.nextInt(1_000_000)); otps.save(normalized, otp);
        kafka.send("email-events", id.toString(), new EmailEvent(UUID.randomUUID(),"USER_REGISTRATION",normalized,locale.getLanguage(),java.util.Map.of("username",displayName,"otp",otp)));
        return id;
    }
    public AuthTokens verifyOtp(String email,String otp) {
        String normalized=email.trim().toLowerCase(Locale.ROOT); String stored=otps.consume(normalized);
        if (stored==null || !stored.equals(otp)) throw new BusinessException(ErrorCode.AUTH_010_INVALID_OTP,HttpStatus.UNAUTHORIZED);
        jdbc.update("UPDATE users SET is_email_verified=true WHERE email=?",normalized);
        return issueForEmail(normalized);
    }
    public AuthTokens rotate(String token) { UUID id=refresh.ownerOf(token); var rotated=refresh.rotate(id,token); return userTokens(id,rotated); }
    @Transactional public AuthTokens oauthLogin(String email,String name){String normalized=email.trim().toLowerCase(Locale.ROOT);boolean test=isTest(normalized);if(!isAcademic(normalized)&&!test)throw new BusinessException(ErrorCode.AUTH_001_DOMAIN_REJECTED,HttpStatus.FORBIDDEN);List<UUID> ids=jdbc.query("SELECT id FROM users WHERE email=? AND deleted_at IS NULL",(rs,n)->(UUID)rs.getObject(1),normalized);UUID id;if(ids.isEmpty()){id=UUID.randomUUID();jdbc.update("INSERT INTO users(id,email,display_name,is_email_verified,is_test_account) VALUES(?,?,?,?,?)",id,normalized,name,true,test);}else{id=ids.getFirst();}return userTokens(id,refresh.issue(id));}
    private AuthTokens issueForEmail(String email) { return jdbc.queryForObject("SELECT id,is_test_account FROM users WHERE email=? AND deleted_at IS NULL",(rs,n)->userTokens((UUID)rs.getObject(1),refresh.issue((UUID)rs.getObject(1))),email); }
    private AuthTokens userTokens(UUID id, RefreshTokenService.IssuedRefreshToken issued) {
        return jdbc.queryForObject("SELECT email,global_role::text,is_test_account FROM users WHERE id=?",(rs,n)->new AuthTokens(jwt.issueAccessToken(id,rs.getString(1),rs.getString(2)),issued.cookie().toString(),rs.getBoolean(3)),id);
    }
    private boolean isAcademic(String email){return email.endsWith(".edu")||email.endsWith(".edu.tr");}
    private boolean isTest(String email){return testDomains!=null&&testDomains.accepts(email,allowTests);}
    public record AuthTokens(String accessToken,@com.fasterxml.jackson.annotation.JsonIgnore String refreshCookie,boolean testAccount){}
    public record EmailEvent(UUID eventId,String eventType,String recipientEmail,String language,java.util.Map<String,String> templateData){}
}
