package app.unifeed.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import app.unifeed.error.BusinessException;
import app.unifeed.notification.NotificationService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
  @Mock JdbcTemplate jdbc;
  @Mock SimpMessagingTemplate webSocket;
  @Mock NotificationService notifications;

  @Test
  void directThreadRejectsConversationWithSelfBeforeQueryingDatabase() {
    ChatService service = new ChatService(jdbc, webSocket, notifications);
    UUID user = UUID.randomUUID();

    assertThatThrownBy(() -> service.directThread(user, user)).isInstanceOf(BusinessException.class);

    verifyNoInteractions(jdbc, webSocket, notifications);
  }

  @Test
  void directThreadReturnsExistingThreadWithoutCreatingAnotherOne() {
    ChatService service = new ChatService(jdbc, webSocket, notifications);
    UUID user = UUID.randomUUID();
    UUID participant = UUID.randomUUID();
    UUID existing = UUID.randomUUID();
    when(jdbc.query(anyString(), any(RowMapper.class), any(), any())).thenReturn(List.of(existing));

    assertThat(service.directThread(user, participant)).isEqualTo(existing);

    verify(jdbc, never()).queryForObject(startsWith("SELECT count(*) FROM users"), eq(Integer.class), any());
    verify(jdbc, never()).update(startsWith("INSERT INTO conversation_threads"), (Object[]) any(Object[].class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void sendPersistsBroadcastsAndNotifiesEveryOtherParticipant() {
    ChatService service = new ChatService(jdbc, webSocket, notifications);
    UUID thread = UUID.randomUUID();
    UUID sender = UUID.randomUUID();
    UUID recipient = UUID.randomUUID();
    when(jdbc.queryForObject(startsWith("SELECT count(*) FROM thread_participants"), eq(Integer.class), eq(thread), eq(sender))).thenReturn(1);
    when(jdbc.query(startsWith("SELECT user_id FROM thread_participants"), any(RowMapper.class), eq(thread), eq(sender))).thenReturn(List.of(recipient));

    ChatService.MessageDto message = service.send(thread, sender, "Merhaba");

    assertThat(message.threadId()).isEqualTo(thread);
    assertThat(message.senderId()).isEqualTo(sender);
    assertThat(message.content()).isEqualTo("Merhaba");
    verify(jdbc).update(startsWith("INSERT INTO direct_messages"), eq(message.id()), eq(thread), eq(sender), eq("Merhaba"));
    verify(webSocket).convertAndSend(eq("/topic/chat/" + thread), eq(message));
    ArgumentCaptor<java.util.Map<String, Object>> payload = ArgumentCaptor.forClass(java.util.Map.class);
    verify(notifications).create(eq(recipient), eq("DIRECT_MESSAGE"), payload.capture());
    assertThat(payload.getValue()).containsEntry("threadId", thread).containsEntry("messageId", message.id()).containsEntry("senderId", sender).containsEntry("content", "Merhaba");
  }
}
