package app.unifeed.feed;
import app.unifeed.common.CurrentUser; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/posts") public class PostController {private final PostService service;public PostController(PostService service){this.service=service;}
 @PostMapping Map<String,UUID> create(@Valid @RequestBody CreatePost r,Authentication a){return Map.of("id",service.create(CurrentUser.id(a),r));}
 @GetMapping("/{id}") PostDto get(@PathVariable UUID id,Authentication a){return service.get(id,CurrentUser.id(a));}
 @DeleteMapping("/{id}") void delete(@PathVariable UUID id,Authentication a){service.delete(id,CurrentUser.id(a));}
 record CreatePost(@NotBlank @Size(max=5000)String content,@Size(max=10)List<@NotBlank String>mediaPaths){} public record PostDto(UUID id,UUID authorId,String authorName,String authorAvatarPath,String content,int likeCount,int commentCount,Instant createdAt,boolean liked,List<String>mediaPaths){} }
