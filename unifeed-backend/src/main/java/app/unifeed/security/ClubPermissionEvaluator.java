package app.unifeed.security;

import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("clubPermissionEvaluator")
public class ClubPermissionEvaluator {
    private static final Duration TTL = Duration.ofSeconds(60);
    private final JdbcTemplate jdbc; private final StringRedisTemplate redis;
    public ClubPermissionEvaluator(JdbcTemplate jdbc, StringRedisTemplate redis) { this.jdbc = jdbc; this.redis = redis; }
    public boolean hasRole(UUID clubId, Authentication authentication, String requiredRole) {
        UUID userId = UUID.fromString(authentication.getName()); String key = "club-role:" + userId + ":" + clubId;
        String role = redis.opsForValue().get(key);
        if (role == null) {
            role = jdbc.query("SELECT role::text FROM club_members WHERE club_id=? AND user_id=?", rs -> rs.next() ? rs.getString(1) : null, clubId, userId);
            if (role != null) redis.opsForValue().set(key, role, TTL);
        }
        return requiredRole.equals(role);
    }
    public void invalidate(UUID clubId, UUID userId) { redis.delete("club-role:" + userId + ":" + clubId); }
}
