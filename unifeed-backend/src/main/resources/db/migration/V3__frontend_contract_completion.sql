ALTER TABLE users ADD COLUMN bio VARCHAR(500);
ALTER TABLE users ADD COLUMN department VARCHAR(160);
ALTER TABLE users ADD COLUMN academic_year VARCHAR(40);

ALTER TABLE clubs ADD COLUMN category VARCHAR(80);
ALTER TABLE clubs ADD COLUMN logo_path TEXT;

ALTER TABLE events ADD COLUMN category VARCHAR(80);

ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'DIRECT_MESSAGE';
ALTER TYPE notification_type ADD VALUE IF NOT EXISTS 'ANNOUNCEMENT';

CREATE INDEX idx_clubs_status_name ON clubs(status, name) WHERE deleted_at IS NULL;
CREATE INDEX idx_events_starts_at ON events(starts_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_display_name ON users(display_name) WHERE deleted_at IS NULL;
