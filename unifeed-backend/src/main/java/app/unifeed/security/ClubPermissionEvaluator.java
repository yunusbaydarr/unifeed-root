package app.unifeed.security;

import java.time.Duration;
import java.util.UUID;
import app.unifeed.cache.ClubRoleCache;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("clubPermissionEvaluator")
public class ClubPermissionEvaluator {
    private static final Duration TTL = Duration.ofSeconds(60);
    private final JdbcTemplate jdbc; private final ClubRoleCache cache;
    public ClubPermissionEvaluator(JdbcTemplate jdbc, ClubRoleCache cache) { this.jdbc = jdbc; this.cache = cache; }
    public boolean hasRole(UUID clubId, Authentication authentication, String requiredRole) {
        UUID userId = UUID.fromString(authentication.getName()); String role = cache.get(userId, clubId);
        if (role == null) {
            role = jdbc.query("SELECT role::text FROM club_members WHERE club_id=? AND user_id=?", rs -> rs.next() ? rs.getString(1) : null, clubId, userId);
            if (role != null) cache.put(userId, clubId, role);
        }
        return requiredRole.equals(role);
    }
    public void invalidate(UUID clubId, UUID userId) { cache.evict(userId, clubId); }
}
