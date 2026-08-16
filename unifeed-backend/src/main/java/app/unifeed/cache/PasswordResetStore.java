package app.unifeed.cache;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetStore {
    private static final Duration TTL = Duration.ofMinutes(15);
    private final StringRedisTemplate redis;

    public PasswordResetStore(StringRedisTemplate redis) { this.redis = redis; }
    public void save(String email, String token) { redis.opsForValue().set("password-reset:" + email, token, TTL); }
    public String consume(String email) {
        String key = "password-reset:" + email;
        String token = redis.opsForValue().get(key);
        redis.delete(key);
        return token;
    }
}
