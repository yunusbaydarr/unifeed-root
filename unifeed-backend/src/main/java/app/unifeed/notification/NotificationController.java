package app.unifeed.notification;
import java.util.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/notifications") public class NotificationController {private final NotificationService service;private final app.unifeed.club.ClubEventService invitations;public NotificationController(NotificationService service,app.unifeed.club.ClubEventService invitations){this.service=service;this.invitations=invitations;}private UUID user(Authentication a){return UUID.fromString(a.getName());}
 @GetMapping List<NotificationService.NotificationDto> list(Authentication a,@RequestParam(defaultValue="ALL")String filter,@RequestParam(defaultValue="50")int limit){return service.list(user(a),filter,limit);}
 @PutMapping("/{id}/read") void read(@PathVariable UUID id,Authentication a){service.markRead(id,user(a));}
 @PutMapping("/read-all") void readAll(Authentication a){service.markAllRead(user(a));}
 @PutMapping("/invitations/{id}") void respond(@PathVariable UUID id,@RequestParam boolean accept,Authentication a){invitations.respondInvitation(id,user(a),accept);}}
