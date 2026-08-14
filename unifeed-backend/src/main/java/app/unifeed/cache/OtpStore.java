package app.unifeed.cache;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OtpStore {
    private static final Duration TTL = Duration.ofMinutes(3);
    private final StringRedisTemplate redis;

    public OtpStore(StringRedisTemplate redis) { this.redis = redis; }
    public void save(String email, String otp) { redis.opsForValue().set("otp:" + email, otp, TTL); }
    public String consume(String email) { String value = redis.opsForValue().get("otp:" + email); redis.delete("otp:" + email); return value; }
}
