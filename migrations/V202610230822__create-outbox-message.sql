-- Transactional outbox: rows are written in the same transaction as the aggregate change and
-- relayed to RabbitMQ afterwards, so a message is published if and only if the change commits.
CREATE TABLE IF NOT EXISTS outbox_message
(
    id           UUID PRIMARY KEY,
    exchange     TEXT        NOT NULL,
    routing_key  TEXT        NOT NULL,
    message_type TEXT        NOT NULL,
    payload      BYTEA       NOT NULL,
    created      TIMESTAMPTZ NOT NULL,
    sent         TIMESTAMPTZ,
    attempts     INTEGER     NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS outbox_message_unsent_idx ON outbox_message (created) WHERE sent IS NULL;
