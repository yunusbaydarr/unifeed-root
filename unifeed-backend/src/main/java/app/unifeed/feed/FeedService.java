package app.unifeed.feed;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FeedService {
    private final JdbcTemplate jdbc; public FeedService(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public FeedPage page(UUID userId,int limit,Instant cursorAt,UUID cursorId){
        int safe=Math.min(Math.max(limit,1),50); Instant at=cursorAt==null?Instant.now():cursorAt; UUID id=cursorId==null?new UUID(-1L,-1L):cursorId;
        String sql="""
          SELECT p.id,p.author_id,u.display_name,p.content,p.like_count,p.comment_count,p.created_at,
          EXISTS(SELECT 1 FROM post_likes l WHERE l.post_id=p.id AND l.user_id=?) liked
          FROM posts p JOIN users u ON u.id=p.author_id
          WHERE p.deleted_at IS NULL AND u.deleted_at IS NULL
          AND (p.author_id=? OR EXISTS(SELECT 1 FROM follows f WHERE f.follower_id=? AND f.following_id=p.author_id))
          AND (p.created_at,p.id)<(?,?) ORDER BY p.created_at DESC,p.id DESC LIMIT ?
          """;
        List<PostCard> items=jdbc.query(sql,(rs,n)->new PostCard((UUID)rs.getObject(1),(UUID)rs.getObject(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getInt(6),rs.getTimestamp(7).toInstant(),rs.getBoolean(8)),userId,userId,userId,Timestamp.from(at),id,safe);
        String next=items.isEmpty()?null:items.getLast().createdAt()+"_"+items.getLast().id(); return new FeedPage(items,next);
    }
    public record PostCard(UUID id,UUID authorId,String author,String content,int likeCount,int commentCount,Instant createdAt,boolean liked){}
    public record FeedPage(List<PostCard> items,String nextCursor){}
}
