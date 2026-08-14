CREATE TYPE conversation_type AS ENUM ('DIRECT','CLUB');
ALTER TABLE conversation_threads ADD COLUMN type conversation_type NOT NULL DEFAULT 'DIRECT';
ALTER TABLE conversation_threads ADD COLUMN club_id UUID REFERENCES clubs(id);
ALTER TABLE direct_messages ADD COLUMN read_at TIMESTAMPTZ;
CREATE INDEX idx_conversation_threads_club_id ON conversation_threads(club_id);
CREATE TABLE processed_events (
  event_id UUID PRIMARY KEY,
  event_type VARCHAR(64) NOT NULL,
  processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE users ADD CONSTRAINT chk_test_account_not_admin CHECK (NOT is_test_account OR global_role <> 'SYSTEM_ADMIN');
