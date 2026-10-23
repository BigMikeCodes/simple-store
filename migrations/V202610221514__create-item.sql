CREATE TABLE IF NOT EXISTS item
(
    id          UUID PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT         NOT NULL,
    created     TIMESTAMPTZ  NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS item_code_uidx ON item (code);
