package app.unifeed.feed;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class PostRepository {
    private final JdbcTemplate jdbc;
    PostRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    void insert(UUID id, UUID authorId, String content) { jdbc.update("INSERT INTO posts(id,author_id,content) VALUES(?,?,?)", id, authorId, content); }
    void addMedia(UUID postId, List<String> paths) { for (int i = 0; i < paths.size(); i++) jdbc.update("INSERT INTO post_media(id,post_id,media_path,order_index,media_type) VALUES(?,?,?,?,?)", UUID.randomUUID(), postId, paths.get(i), i + 1, "IMAGE"); }
    List<PostController.PostDto> findById(UUID id, UUID viewerId) { return jdbc.query("SELECT p.id,p.author_id,u.display_name,u.avatar_path,p.content,p.like_count,p.comment_count,p.created_at,EXISTS(SELECT 1 FROM post_likes WHERE post_id=p.id AND user_id=?),COALESCE((SELECT array_agg(media_path ORDER BY order_index) FROM post_media WHERE post_id=p.id AND deleted_at IS NULL),ARRAY[]::text[]) FROM posts p JOIN users u ON u.id=p.author_id WHERE p.id=? AND p.deleted_at IS NULL", (rs,n) -> new PostController.PostDto((UUID)rs.getObject(1),(UUID)rs.getObject(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getInt(6),rs.getInt(7),rs.getTimestamp(8).toInstant(),rs.getBoolean(9),List.of((String[])rs.getArray(10).getArray())), viewerId, id); }
    boolean softDelete(UUID id, UUID authorId) { return jdbc.update("UPDATE posts SET deleted_at=now() WHERE id=? AND author_id=? AND deleted_at IS NULL", id, authorId) > 0; }
}
