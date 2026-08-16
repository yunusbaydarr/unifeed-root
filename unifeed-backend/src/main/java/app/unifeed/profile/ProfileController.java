package app.unifeed.profile;
import jakarta.validation.Valid; import java.util.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/users") public class ProfileController {private final ProfileService service;public ProfileController(ProfileService service){this.service=service;}private UUID current(Authentication a){return UUID.fromString(a.getName());}
 @GetMapping("/me") ProfileService.ProfileDto me(Authentication a){UUID id=current(a);return service.get(id,id);}
 @PatchMapping("/me") ProfileService.ProfileDto update(@Valid @RequestBody ProfileService.UpdateProfileRequest r,Authentication a){return service.update(current(a),r);}
 @GetMapping("/{id}") ProfileService.ProfileDto profile(@PathVariable UUID id,Authentication a){return service.get(id,current(a));}
 @GetMapping("/{id}/followers") List<ProfileService.ProfileListItemDto> followers(@PathVariable UUID id,@RequestParam(defaultValue="50")int limit){return service.followers(id,limit);}
 @GetMapping("/{id}/following") List<ProfileService.ProfileListItemDto> following(@PathVariable UUID id,@RequestParam(defaultValue="50")int limit){return service.following(id,limit);}
 @GetMapping("/{id}/clubs") List<ProfileService.MemberClubDto> clubs(@PathVariable UUID id,@RequestParam(defaultValue="50")int limit){return service.clubs(id,limit);}
 @GetMapping("/{id}/posts") List<ProfileService.UserPostDto> posts(@PathVariable UUID id,@RequestParam(defaultValue="20")int limit){return service.posts(id,limit);}}
