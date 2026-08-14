package app.unifeed.story;
import java.time.*; import java.util.UUID; import org.springframework.data.redis.core.StringRedisTemplate; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service public class StoryService { private final JdbcTemplate jdbc; private final StringRedisTemplate redis; public StoryService(JdbcTemplate j,StringRedisTemplate r){jdbc=j;redis=r;}
 @Transactional public UUID create(UUID user,String path){UUID id=UUID.randomUUID();Instant expiry=Instant.now().plus(24,java.time.temporal.ChronoUnit.HOURS);jdbc.update("INSERT INTO stories(id,author_id,media_path,expires_at) VALUES(?,?,?,?)",id,user,path,java.sql.Timestamp.from(expiry));redis.opsForZSet().add("active_stories",id.toString(),expiry.toEpochMilli());return id;}
 @Scheduled(fixedRate=60000) @Transactional public void expire(){jdbc.update("UPDATE stories SET deleted_at=now() WHERE deleted_at IS NULL AND expires_at<=now()");redis.opsForZSet().removeRangeByScore("active_stories",0,Instant.now().toEpochMilli());}
}
