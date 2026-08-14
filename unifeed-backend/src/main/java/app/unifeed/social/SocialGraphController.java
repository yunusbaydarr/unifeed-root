package app.unifeed.social;
import jakarta.validation.constraints.NotBlank; import java.util.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1") public class SocialGraphController {
 private final SocialGraphService service; public SocialGraphController(SocialGraphService service){this.service=service;} private UUID user(Authentication a){return UUID.fromString(a.getName());}
 @PutMapping("/posts/{id}/like") Map<String,Boolean> like(@PathVariable UUID id,Authentication a){service.like(id,user(a));return Map.of("liked",true);}
 @DeleteMapping("/posts/{id}/like") Map<String,Boolean> unlike(@PathVariable UUID id,Authentication a){service.unlike(id,user(a));return Map.of("liked",false);}
 @PostMapping("/posts/{id}/comments") Map<String,UUID> comment(@PathVariable UUID id,@RequestBody CommentRequest r,Authentication a){return Map.of("id",service.comment(id,user(a),r.parentId,r.content));}
 @PutMapping("/users/{id}/follow") Map<String,Boolean> follow(@PathVariable UUID id,Authentication a){service.follow(user(a),id);return Map.of("following",true);}
 record CommentRequest(UUID parentId,@NotBlank String content){}
}
