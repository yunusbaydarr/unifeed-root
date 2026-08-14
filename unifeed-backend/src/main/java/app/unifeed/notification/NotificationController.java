package app.unifeed.notification;
import java.util.*; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.security.core.Authentication; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/notifications") public class NotificationController {private final JdbcTemplate jdbc;public NotificationController(JdbcTemplate j){jdbc=j;}
 @GetMapping List<Map<String,Object>> list(Authentication a){return jdbc.queryForList("SELECT id,type::text,payload,read_at,created_at FROM notifications WHERE recipient_id=? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT 100",UUID.fromString(a.getName()));}
 @PutMapping("/{id}/read") @Transactional void read(@PathVariable UUID id,Authentication a){jdbc.update("UPDATE notifications SET read_at=COALESCE(read_at,now()) WHERE id=? AND recipient_id=? AND deleted_at IS NULL",id,UUID.fromString(a.getName()));}
}
