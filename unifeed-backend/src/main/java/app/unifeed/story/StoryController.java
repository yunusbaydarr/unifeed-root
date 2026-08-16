package app.unifeed.story;
import jakarta.validation.Valid; import jakarta.validation.constraints.NotBlank; import java.util.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/stories") public class StoryController {private final StoryService service;public StoryController(StoryService service){this.service=service;}
 @PostMapping Map<String,UUID> create(@Valid @RequestBody CreateStoryRequest r,Authentication a){return Map.of("id",service.create(UUID.fromString(a.getName()),r.mediaPath()));}
 @GetMapping List<StoryService.StoryDto> active(Authentication a){return service.active(UUID.fromString(a.getName()));}
 @PutMapping("/{id}/view") void view(@PathVariable UUID id,Authentication a){service.view(id,UUID.fromString(a.getName()));}
 record CreateStoryRequest(@NotBlank String mediaPath){} }
