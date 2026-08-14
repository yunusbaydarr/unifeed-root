package app.unifeed.social;
import app.unifeed.error.*; import java.util.*; import org.springframework.http.HttpStatus; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service public class SocialGraphService {
 private final JdbcTemplate jdbc; public SocialGraphService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @Transactional public boolean like(UUID post,UUID user){return jdbc.update("INSERT INTO post_likes(post_id,user_id) VALUES(?,?) ON CONFLICT DO NOTHING",post,user)>0;}
 @Transactional public boolean unlike(UUID post,UUID user){return jdbc.update("DELETE FROM post_likes WHERE post_id=? AND user_id=?",post,user)>0;}
 @Transactional public UUID comment(UUID post,UUID user,UUID parent,String content){if(parent!=null){Integer depth=jdbc.queryForObject("SELECT CASE WHEN parent_comment_id IS NULL THEN 1 ELSE 2 END FROM comments WHERE id=? AND post_id=?",Integer.class,parent,post);if(depth==null||depth>1)throw new BusinessException(ErrorCode.COMMON_409_CONFLICT,HttpStatus.CONFLICT);}UUID id=UUID.randomUUID();jdbc.update("INSERT INTO comments(id,post_id,user_id,parent_comment_id,content) VALUES(?,?,?,?,?)",id,post,user,parent,content);return id;}
 @Transactional public boolean follow(UUID from,UUID to){return jdbc.update("INSERT INTO follows(follower_id,following_id) VALUES(?,?) ON CONFLICT DO NOTHING",from,to)>0;}
}
