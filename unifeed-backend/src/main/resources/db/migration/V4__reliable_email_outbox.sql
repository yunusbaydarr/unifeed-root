CREATE TABLE email_outbox (
    id UUID PRIMARY KEY,
    topic VARCHAR(100) NOT NULL,
    event_key VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    last_error VARCHAR(500)
);

CREATE INDEX idx_email_outbox_pending
    ON email_outbox(next_attempt_at, created_at)
    WHERE published_at IS NULL;
