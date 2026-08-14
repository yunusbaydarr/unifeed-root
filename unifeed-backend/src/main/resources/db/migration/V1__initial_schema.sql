CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN NEW.updated_at = now(); RETURN NEW; END; $$;
CREATE TYPE global_role AS ENUM ('SYSTEM_ADMIN','STUDENT');
CREATE TYPE club_member_role AS ENUM ('CLUB_ADMIN','CLUB_MEMBER');
CREATE TYPE club_status AS ENUM ('PENDING_APPROVAL','ACTIVE','SUSPENDED','CLOSED');
CREATE TYPE notification_type AS ENUM ('LIKE','COMMENT','FOLLOW','CLUB_INVITE','EVENT_REMINDER','CLUB_ENROLLMENT');
CREATE TABLE users (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), email VARCHAR(320) NOT NULL UNIQUE, display_name VARCHAR(100) NOT NULL, password_hash VARCHAR(255), avatar_path TEXT, global_role global_role NOT NULL DEFAULT 'STUDENT', is_email_verified BOOLEAN NOT NULL DEFAULT false, is_test_account BOOLEAN NOT NULL DEFAULT false, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE clubs (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), name VARCHAR(120) NOT NULL UNIQUE, description TEXT, cover_path TEXT, status club_status NOT NULL DEFAULT 'PENDING_APPROVAL', created_by UUID NOT NULL REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE club_members (club_id UUID NOT NULL REFERENCES clubs(id), user_id UUID NOT NULL REFERENCES users(id), role club_member_role NOT NULL DEFAULT 'CLUB_MEMBER', created_at TIMESTAMPTZ NOT NULL DEFAULT now(), PRIMARY KEY(club_id,user_id));
CREATE TABLE posts (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), author_id UUID NOT NULL REFERENCES users(id), content TEXT, like_count INTEGER NOT NULL DEFAULT 0 CHECK(like_count>=0), comment_count INTEGER NOT NULL DEFAULT 0 CHECK(comment_count>=0), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE post_media (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), post_id UUID NOT NULL REFERENCES posts(id), media_path TEXT NOT NULL, order_index SMALLINT NOT NULL CHECK(order_index BETWEEN 1 AND 10), media_type VARCHAR(32) NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0, UNIQUE(post_id,order_index));
CREATE TABLE post_likes (post_id UUID NOT NULL REFERENCES posts(id), user_id UUID NOT NULL REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), PRIMARY KEY(post_id,user_id));
CREATE TABLE comments (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), post_id UUID NOT NULL REFERENCES posts(id), user_id UUID NOT NULL REFERENCES users(id), parent_comment_id UUID REFERENCES comments(id), content TEXT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE follows (follower_id UUID NOT NULL REFERENCES users(id), following_id UUID NOT NULL REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), PRIMARY KEY(follower_id,following_id), CHECK(follower_id<>following_id));
CREATE TABLE stories (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), author_id UUID NOT NULL REFERENCES users(id), media_path TEXT NOT NULL, expires_at TIMESTAMPTZ NOT NULL, view_count INTEGER NOT NULL DEFAULT 0 CHECK(view_count>=0), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE events (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), club_id UUID NOT NULL REFERENCES clubs(id), created_by UUID NOT NULL REFERENCES users(id), title VARCHAR(160) NOT NULL, description TEXT, poster_path TEXT, starts_at TIMESTAMPTZ NOT NULL, ends_at TIMESTAMPTZ, location VARCHAR(255), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0, CHECK(ends_at IS NULL OR ends_at>=starts_at));
CREATE TABLE event_attendees (event_id UUID NOT NULL REFERENCES events(id), user_id UUID NOT NULL REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), PRIMARY KEY(event_id,user_id));
CREATE TABLE club_threads (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), club_id UUID NOT NULL REFERENCES clubs(id), title VARCHAR(160) NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE club_thread_messages (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), thread_id UUID NOT NULL REFERENCES club_threads(id), sender_id UUID NOT NULL REFERENCES users(id), content TEXT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE conversation_threads (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE thread_participants (thread_id UUID NOT NULL REFERENCES conversation_threads(id), user_id UUID NOT NULL REFERENCES users(id), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), PRIMARY KEY(thread_id,user_id));
CREATE TABLE direct_messages (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), thread_id UUID NOT NULL REFERENCES conversation_threads(id), sender_id UUID NOT NULL REFERENCES users(id), content TEXT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE TABLE notifications (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), recipient_id UUID NOT NULL REFERENCES users(id), type notification_type NOT NULL, payload JSONB NOT NULL DEFAULT '{}'::jsonb, read_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ, version BIGINT NOT NULL DEFAULT 0);
CREATE INDEX idx_posts_created_at_id ON posts(created_at DESC,id DESC); CREATE INDEX idx_club_members_user_id ON club_members(user_id); CREATE INDEX idx_comments_post_id ON comments(post_id); CREATE INDEX idx_events_club_id ON events(club_id); CREATE INDEX idx_event_attendees_user_id ON event_attendees(user_id); CREATE INDEX idx_direct_messages_thread_created_at ON direct_messages(thread_id,created_at DESC); CREATE INDEX idx_notifications_recipient_created_at ON notifications(recipient_id,created_at DESC);
CREATE INDEX idx_posts_author_id ON posts(author_id); CREATE INDEX idx_post_media_post_id ON post_media(post_id); CREATE INDEX idx_post_likes_user_id ON post_likes(user_id); CREATE INDEX idx_comments_user_id ON comments(user_id); CREATE INDEX idx_comments_parent_id ON comments(parent_comment_id); CREATE INDEX idx_follows_following_id ON follows(following_id); CREATE INDEX idx_stories_author_id ON stories(author_id); CREATE INDEX idx_stories_expires_at ON stories(expires_at); CREATE INDEX idx_club_threads_club_id ON club_threads(club_id); CREATE INDEX idx_club_thread_messages_thread_id ON club_thread_messages(thread_id); CREATE INDEX idx_thread_participants_user_id ON thread_participants(user_id); CREATE INDEX idx_direct_messages_sender_id ON direct_messages(sender_id);
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_clubs_updated_at BEFORE UPDATE ON clubs FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_posts_updated_at BEFORE UPDATE ON posts FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_post_media_updated_at BEFORE UPDATE ON post_media FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_comments_updated_at BEFORE UPDATE ON comments FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_stories_updated_at BEFORE UPDATE ON stories FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_events_updated_at BEFORE UPDATE ON events FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_club_threads_updated_at BEFORE UPDATE ON club_threads FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_club_thread_messages_updated_at BEFORE UPDATE ON club_thread_messages FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_conversation_threads_updated_at BEFORE UPDATE ON conversation_threads FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_direct_messages_updated_at BEFORE UPDATE ON direct_messages FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_notifications_updated_at BEFORE UPDATE ON notifications FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE OR REPLACE FUNCTION refresh_post_counts() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE target_post_id UUID;
BEGIN
  target_post_id := COALESCE(NEW.post_id, OLD.post_id);
  UPDATE posts SET like_count=(SELECT count(*) FROM post_likes WHERE post_id=target_post_id),
    comment_count=(SELECT count(*) FROM comments WHERE post_id=target_post_id AND deleted_at IS NULL)
    WHERE id=target_post_id;
  RETURN COALESCE(NEW, OLD);
END; $$;
CREATE TRIGGER trg_post_likes_counts AFTER INSERT OR DELETE ON post_likes FOR EACH ROW EXECUTE FUNCTION refresh_post_counts();
CREATE TRIGGER trg_comments_counts AFTER INSERT OR UPDATE OF deleted_at OR DELETE ON comments FOR EACH ROW EXECUTE FUNCTION refresh_post_counts();
