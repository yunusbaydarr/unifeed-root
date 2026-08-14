package app.unifeed.cache;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class StoryIndex {
    private static final String KEY = "active_stories";
    private final StringRedisTemplate redis;
    public StoryIndex(StringRedisTemplate redis) { this.redis = redis; }
    public void add(UUID storyId, Instant expiry) { redis.opsForZSet().add(KEY, storyId.toString(), expiry.toEpochMilli()); }
    public void removeExpired(Instant now) { redis.opsForZSet().removeRangeByScore(KEY, 0, now.toEpochMilli()); }
}
