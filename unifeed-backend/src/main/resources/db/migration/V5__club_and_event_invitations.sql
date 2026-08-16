ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'CLUB_INVITATION';
ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'EVENT_INVITATION';

CREATE TYPE invitation_status AS ENUM ('PENDING','ACCEPTED','DECLINED');
CREATE TABLE invitations (
  id UUID PRIMARY KEY,
  kind VARCHAR(16) NOT NULL CHECK (kind IN ('CLUB','EVENT')),
  club_id UUID REFERENCES clubs(id) ON DELETE CASCADE,
  event_id UUID REFERENCES events(id) ON DELETE CASCADE,
  inviter_id UUID NOT NULL REFERENCES users(id),
  invitee_id UUID NOT NULL REFERENCES users(id),
  status invitation_status NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  responded_at TIMESTAMPTZ,
  CHECK ((kind='CLUB' AND club_id IS NOT NULL AND event_id IS NULL) OR (kind='EVENT' AND event_id IS NOT NULL AND club_id IS NOT NULL))
);
CREATE UNIQUE INDEX ux_pending_club_invitation ON invitations(club_id, invitee_id) WHERE kind='CLUB' AND status='PENDING';
CREATE UNIQUE INDEX ux_pending_event_invitation ON invitations(event_id, invitee_id) WHERE kind='EVENT' AND status='PENDING';
