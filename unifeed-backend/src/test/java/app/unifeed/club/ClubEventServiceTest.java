package app.unifeed.club;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import app.unifeed.error.BusinessException;
import app.unifeed.mail.EmailOutboxService;
import app.unifeed.notification.NotificationService;
import app.unifeed.security.ClubPermissionEvaluator;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class ClubEventServiceTest {
  @Mock JdbcTemplate jdbc;
  @Mock ClubPermissionEvaluator permissions;
  @Mock EmailOutboxService emailOutbox;
  @Mock NotificationService notifications;

  @Test
  void clubInvitationRejectsInvitingTheSameUser() {
    ClubEventService service = new ClubEventService(jdbc, permissions, emailOutbox, notifications);
    UUID user = UUID.randomUUID();

    assertThatThrownBy(() -> service.inviteClub(UUID.randomUUID(), user, user)).isInstanceOf(BusinessException.class);

    verifyNoInteractions(jdbc, permissions, emailOutbox, notifications);
  }

  @Test
  void acceptingEventInvitationEnrollsUserInClubAndEvent() {
    ClubEventService service = new ClubEventService(jdbc, permissions, emailOutbox, notifications);
    UUID invitation = UUID.randomUUID();
    UUID user = UUID.randomUUID();
    UUID club = UUID.randomUUID();
    UUID event = UUID.randomUUID();
    when(jdbc.queryForMap(startsWith("SELECT kind,club_id,event_id FROM invitations"), eq(invitation), eq(user)))
        .thenReturn(Map.of("kind", "EVENT", "club_id", club, "event_id", event));
    when(jdbc.update(startsWith("UPDATE invitations SET status"), eq("ACCEPTED"), eq(invitation))).thenReturn(1);

    service.respondInvitation(invitation, user, true);

    verify(jdbc).update(startsWith("INSERT INTO club_members"), eq(club), eq(user));
    verify(permissions).invalidate(club, user);
    verify(notifications).create(eq(user), eq("CLUB_ENROLLMENT"), eq(Map.of("clubId", club)));
    verify(jdbc).update(startsWith("INSERT INTO event_attendees"), eq(event), eq(user));
  }

  @Test
  void decliningInvitationDoesNotCreateMembershipOrEnrollmentNotification() {
    ClubEventService service = new ClubEventService(jdbc, permissions, emailOutbox, notifications);
    UUID invitation = UUID.randomUUID();
    UUID user = UUID.randomUUID();
    when(jdbc.queryForMap(startsWith("SELECT kind,club_id,event_id FROM invitations"), eq(invitation), eq(user)))
        .thenReturn(Map.of("kind", "CLUB", "club_id", UUID.randomUUID()));
    when(jdbc.update(startsWith("UPDATE invitations SET status"), eq("DECLINED"), eq(invitation))).thenReturn(1);

    service.respondInvitation(invitation, user, false);

    verify(jdbc, never()).update(startsWith("INSERT INTO club_members"), any(), any());
    verifyNoInteractions(permissions, notifications, emailOutbox);
  }
}
