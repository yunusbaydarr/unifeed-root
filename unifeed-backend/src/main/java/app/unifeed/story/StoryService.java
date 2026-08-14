package app.unifeed.story;
import app.unifeed.cache.StoryIndex; import java.time.*; import java.util.UUID; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service public class StoryService { private final JdbcTemplate jdbc; private final StoryIndex index; public StoryService(JdbcTemplate j,StoryIndex i){jdbc=j;index=i;}
 @Transactional public UUID create(UUID user,String path){UUID id=UUID.randomUUID();Instant expiry=Instant.now().plus(24,java.time.temporal.ChronoUnit.HOURS);jdbc.update("INSERT INTO stories(id,author_id,media_path,expires_at) VALUES(?,?,?,?)",id,user,path,java.sql.Timestamp.from(expiry));index.add(id,expiry);return id;}
 @Scheduled(fixedRate=60000) @Transactional public void expire(){Instant now=Instant.now();jdbc.update("UPDATE stories SET deleted_at=now() WHERE deleted_at IS NULL AND expires_at<=now()");index.removeExpired(now);}
}
