package app.unifeed.chat;
import jakarta.validation.Valid; import jakarta.validation.constraints.NotBlank; import java.security.Principal; import java.util.*; import org.springframework.messaging.handler.annotation.*; import org.springframework.stereotype.Controller; import org.springframework.web.bind.annotation.*;
@Controller public class ChatController {private final ChatService service;public ChatController(ChatService service){this.service=service;}private UUID user(Principal p){return UUID.fromString(p.getName());}
 @MessageMapping("/chat.send/{threadId}") public void send(@DestinationVariable UUID threadId,@Valid ChatMessage input,Principal p){service.send(threadId,user(p),input.content());}
 @GetMapping("/api/v1/chat/threads") @ResponseBody List<ChatService.ThreadDto> threads(Principal p){return service.threads(user(p));}
 @PostMapping("/api/v1/chat/threads/direct") @ResponseBody Map<String,UUID> direct(@Valid @RequestBody DirectThreadRequest r,Principal p){return Map.of("id",service.directThread(user(p),r.participantId()));}
 @GetMapping("/api/v1/chat/threads/{threadId}/messages") @ResponseBody List<ChatService.MessageDto> history(@PathVariable UUID threadId,@RequestParam(defaultValue="50")int limit,Principal p){return service.history(threadId,user(p),limit);}
 @PutMapping("/api/v1/chat/threads/{threadId}/read") @ResponseBody void read(@PathVariable UUID threadId,Principal p){service.markRead(threadId,user(p));}
 public record ChatMessage(@NotBlank String content){} public record DirectThreadRequest(UUID participantId){} }
