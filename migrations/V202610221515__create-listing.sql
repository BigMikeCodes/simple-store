CREATE TABLE IF NOT EXISTS listing
(
    id          UUID PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT         NOT NULL,
    status      VARCHAR(20)  NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'WITHDRAWN')),
    created     TIMESTAMPTZ  NOT NULL,
    updated     TIMESTAMPTZ  NOT NULL,
    published   TIMESTAMPTZ,
    withdrawn   TIMESTAMPTZ,
    version     INTEGER      NOT NULL
);

CREATE INDEX IF NOT EXISTS listing_status_idx ON listing (status);

CREATE TABLE IF NOT EXISTS listing_item
(
    listing_id UUID    NOT NULL REFERENCES listing (id) ON DELETE CASCADE,
    item_id    UUID    NOT NULL REFERENCES item (id),
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (listing_id, item_id)
);

CREATE INDEX IF NOT EXISTS listing_item_item_id_idx ON listing_item (item_id);
