package app.unifeed.club;
import java.time.Instant; import java.util.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1") public class ClubEventController {private final ClubEventService s;public ClubEventController(ClubEventService s){this.s=s;}private UUID u(Authentication a){return UUID.fromString(a.getName());}
 @PostMapping("/clubs") Map<String,UUID> club(@RequestBody ClubRequest r,Authentication a){return Map.of("id",s.createClub(u(a),r.name,r.description));}
 @PostMapping("/clubs/{clubId}/events") @PreAuthorize("@clubPermissionEvaluator.hasRole(#clubId,authentication,'CLUB_ADMIN')") Map<String,UUID> event(@PathVariable UUID clubId,@RequestBody EventRequest r,Authentication a){return Map.of("id",s.createEvent(clubId,u(a),r.title,r.startsAt));}
 @PutMapping("/events/{eventId}/join") void join(@PathVariable UUID eventId,Authentication a){s.joinEvent(u(a),eventId);}
 @GetMapping("/clubs/{clubId}/invite-candidates") List<Map<String,Object>> candidates(@PathVariable UUID clubId,Authentication a){return s.inviteCandidates(clubId,u(a));}
 @GetMapping("/clubs/{clubId}") Map<String,Object> detail(@PathVariable UUID clubId,Authentication a){return s.detail(clubId,u(a));}
 record ClubRequest(String name,String description){} record EventRequest(String title,Instant startsAt){}
}
