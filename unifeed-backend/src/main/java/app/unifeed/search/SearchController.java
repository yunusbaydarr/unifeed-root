package app.unifeed.search;
import java.util.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/search") public class SearchController {private final SearchService service;public SearchController(SearchService service){this.service=service;}
 @GetMapping SearchResult search(@RequestParam String query,@RequestParam(defaultValue="10")int limit){return service.search(query,limit);}
 public record SearchResult(List<UserResult>users,List<ClubResult>clubs,List<EventResult>events){} public record UserResult(UUID id,String displayName,String avatarPath,String department){} public record ClubResult(UUID id,String name,String logoPath,String category){} public record EventResult(UUID id,String title,String posterPath,java.time.Instant startsAt,String location){} }
