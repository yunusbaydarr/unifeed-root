package app.unifeed.feed;
import java.time.Instant; import java.util.UUID; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/feed") public class FeedController {
 private final FeedService service; public FeedController(FeedService service){this.service=service;}
 @GetMapping FeedService.FeedPage page(Authentication a,@RequestParam(defaultValue="20") int limit,@RequestParam(required=false) Instant cursor_created_at,@RequestParam(required=false) UUID cursor_id){return service.page(UUID.fromString(a.getName()),limit,cursor_created_at,cursor_id);}
}
