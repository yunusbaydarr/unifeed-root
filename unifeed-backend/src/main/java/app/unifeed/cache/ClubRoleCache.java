package app.unifeed.cache;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClubRoleCache {
    private static final Duration TTL = Duration.ofSeconds(60);
    private final StringRedisTemplate redis;
    public ClubRoleCache(StringRedisTemplate redis) { this.redis = redis; }
    public String get(UUID userId, UUID clubId) { return redis.opsForValue().get(key(userId, clubId)); }
    public void put(UUID userId, UUID clubId, String role) { redis.opsForValue().set(key(userId, clubId), role, TTL); }
    public void evict(UUID userId, UUID clubId) { redis.delete(key(userId, clubId)); }
    private String key(UUID userId, UUID clubId) { return "club-role:" + userId + ":" + clubId; }
}
