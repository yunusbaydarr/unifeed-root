package app.unifeed.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class RefreshTokenService {
    private static final Duration TTL = Duration.ofDays(7);
    private final StringRedisTemplate redis;
    private final boolean secureCookie;
    public RefreshTokenService(StringRedisTemplate redis,@Value("${app.security.secure-cookie:true}") boolean secureCookie) { this.redis = redis; this.secureCookie=secureCookie; }

    public IssuedRefreshToken issue(UUID userId) {
        String token = userId + "." + KeyGenerators.string().generateKey() + KeyGenerators.string().generateKey();
        redis.opsForValue().set(key(userId), hash(token), TTL);
        return new IssuedRefreshToken(token, cookie(token));
    }

    public IssuedRefreshToken rotate(UUID userId, String presentedToken) {
        String storedHash = redis.opsForValue().get(key(userId));
        if (storedHash == null || !MessageDigest.isEqual(storedHash.getBytes(StandardCharsets.UTF_8), hash(presentedToken).getBytes(StandardCharsets.UTF_8))) {
            throw new BusinessAuthenticationException();
        }
        redis.delete(key(userId));
        return issue(userId);
    }

    public void revoke(UUID userId) { redis.delete(key(userId)); }
    public UUID ownerOf(String token) {
        try { return UUID.fromString(token.substring(0, token.indexOf('.'))); }
        catch (RuntimeException ex) { throw new BusinessAuthenticationException(); }
    }
    private String key(UUID userId) { return "refresh:" + userId; }
    private ResponseCookie cookie(String token) { return ResponseCookie.from("refreshToken", token).httpOnly(true).secure(secureCookie).sameSite("Strict").path("/api/v1/auth").maxAge(TTL).build(); }
    private String hash(String token) {
        try { return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException ex) { throw new IllegalStateException("SHA-256 unavailable", ex); }
    }
    public record IssuedRefreshToken(String value, ResponseCookie cookie) {}
}
