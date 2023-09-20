CREATE TABLE places
(
    id    BIGSERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL
);

CREATE TABLE events
(
    event_no      BIGINT PRIMARY KEY,
    place_id       BIGINT REFERENCES places (id) ON DELETE CASCADE,
    manufacturer VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL
);