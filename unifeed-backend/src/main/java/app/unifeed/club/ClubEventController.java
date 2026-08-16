package app.unifeed.club;
import jakarta.validation.Valid; import java.util.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1") public class ClubEventController {private final ClubEventService service;public ClubEventController(ClubEventService service){this.service=service;}private UUID user(Authentication a){return UUID.fromString(a.getName());}
 @PostMapping("/clubs") Map<String,UUID> club(@Valid @RequestBody ClubEventService.ClubRequest r,Authentication a){return Map.of("id",service.createClub(user(a),r));}
 @GetMapping("/clubs") List<ClubEventService.ClubSummaryDto> clubs(Authentication a,@RequestParam(required=false)String query,@RequestParam(defaultValue="false")boolean mine,@RequestParam(defaultValue="20")int limit){return service.clubs(user(a),query,mine,limit);}
 @GetMapping("/clubs/{clubId}") ClubEventService.ClubDetailDto detail(@PathVariable UUID clubId,Authentication a){return service.detail(clubId,user(a));}
 @PutMapping("/clubs/{clubId}/join") void joinClub(@PathVariable UUID clubId,Authentication a){service.joinClub(user(a),clubId);}
 @GetMapping("/clubs/{clubId}/invite-candidates") List<ClubEventService.InviteCandidateDto> candidates(@PathVariable UUID clubId,Authentication a){return service.inviteCandidates(clubId,user(a));}
 @PostMapping("/clubs/{clubId}/invitations") Map<String,UUID> inviteClub(@PathVariable UUID clubId,@Valid @RequestBody InviteRequest r,Authentication a){return Map.of("id",service.inviteClub(clubId,user(a),r.userId()));}
 @PostMapping("/clubs/{clubId}/events") @PreAuthorize("@clubPermissionEvaluator.hasRole(#clubId,authentication,'CLUB_ADMIN')") Map<String,UUID> event(@PathVariable UUID clubId,@Valid @RequestBody ClubEventService.EventRequest r,Authentication a){return Map.of("id",service.createEvent(clubId,user(a),r));}
 @GetMapping("/events") List<ClubEventService.EventDto> events(Authentication a,@RequestParam(required=false)UUID clubId,@RequestParam(required=false)String query,@RequestParam(defaultValue="20")int limit){return service.events(user(a),clubId,query,limit);}
 @GetMapping("/events/{eventId}") ClubEventService.EventDto event(@PathVariable UUID eventId,Authentication a){return service.event(user(a),eventId);}
 @GetMapping("/events/{eventId}/invite-candidates") List<ClubEventService.InviteCandidateDto> eventCandidates(@PathVariable UUID eventId,Authentication a){return service.eventInviteCandidates(eventId,user(a));}
 @PostMapping("/events/{eventId}/invitations") Map<String,UUID> inviteEvent(@PathVariable UUID eventId,@Valid @RequestBody InviteRequest r,Authentication a){return Map.of("id",service.inviteEvent(eventId,user(a),r.userId()));}
 @PutMapping("/events/{eventId}/join") void join(@PathVariable UUID eventId,Authentication a){service.joinEvent(user(a),eventId);} }
 record InviteRequest(@jakarta.validation.constraints.NotNull UUID userId){}
