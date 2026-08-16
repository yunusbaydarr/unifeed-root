package app.unifeed.feed;
import java.sql.Timestamp; import java.time.Instant; import java.util.*; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.stereotype.Service;
@Service public class FeedService {private final JdbcTemplate jdbc;public FeedService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public FeedPage page(UUID userId,int limit,Instant cursorAt,UUID cursorId){int safe=Math.min(Math.max(limit,1),50);Instant at=cursorAt==null?Instant.now():cursorAt;UUID id=cursorId==null?new UUID(-1L,-1L):cursorId;String sql="""
  SELECT p.id,p.author_id,u.display_name,u.avatar_path,p.content,p.like_count,p.comment_count,p.created_at,
   EXISTS(SELECT 1 FROM post_likes l WHERE l.post_id=p.id AND l.user_id=?),
   COALESCE((SELECT array_agg(pm.media_path ORDER BY pm.order_index) FROM post_media pm WHERE pm.post_id=p.id AND pm.deleted_at IS NULL),ARRAY[]::text[])
  FROM posts p JOIN users u ON u.id=p.author_id WHERE p.deleted_at IS NULL AND u.deleted_at IS NULL
   AND (p.author_id=? OR EXISTS(SELECT 1 FROM follows f WHERE f.follower_id=? AND f.following_id=p.author_id))
   AND (p.created_at,p.id)<(?,?) ORDER BY p.created_at DESC,p.id DESC LIMIT ?
  """;List<PostCard> items=jdbc.query(sql,(rs,n)->new PostCard((UUID)rs.getObject(1),(UUID)rs.getObject(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getInt(6),rs.getInt(7),rs.getTimestamp(8).toInstant(),rs.getBoolean(9),List.of((String[])rs.getArray(10).getArray())),userId,userId,userId,Timestamp.from(at),id,safe);PostCard last=items.isEmpty()?null:items.getLast();return new FeedPage(items,last==null?null:last.createdAt(),last==null?null:last.id());}
 public record PostCard(UUID id,UUID authorId,String authorName,String authorAvatarPath,String content,int likeCount,int commentCount,Instant createdAt,boolean liked,List<String>mediaPaths){}
 public record FeedPage(List<PostCard>items,Instant nextCursorCreatedAt,UUID nextCursorId){} }
