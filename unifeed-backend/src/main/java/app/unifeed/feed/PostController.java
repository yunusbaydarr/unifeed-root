package app.unifeed.feed;
import jakarta.validation.constraints.NotBlank; import java.util.*; import org.springframework.jdbc.core.JdbcTemplate; import org.springframework.security.core.Authentication; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/posts") public class PostController {private final JdbcTemplate jdbc;public PostController(JdbcTemplate j){jdbc=j;}
 @PostMapping @Transactional Map<String,UUID> create(@RequestBody CreatePost r,Authentication a){UUID id=UUID.randomUUID();jdbc.update("INSERT INTO posts(id,author_id,content) VALUES(?,?,?)",id,UUID.fromString(a.getName()),r.content);if(r.mediaPaths!=null)for(int i=0;i<Math.min(r.mediaPaths.size(),10);i++)jdbc.update("INSERT INTO post_media(id,post_id,media_path,order_index,media_type) VALUES(?,?,?,?,?)",UUID.randomUUID(),id,r.mediaPaths.get(i),i+1,"IMAGE");return Map.of("id",id);}
 record CreatePost(@NotBlank String content,List<String> mediaPaths){}
}
