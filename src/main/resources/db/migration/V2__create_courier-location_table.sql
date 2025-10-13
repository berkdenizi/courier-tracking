CREATE TABLE IF NOT EXISTS courier_location
(
    id              BIGSERIAL PRIMARY KEY,
    courier_id      BIGINT NOT NULL,
    lat             DOUBLE PRECISION NOT NULL,
    lng             DOUBLE PRECISION NOT NULL,
    event_time      TIMESTAMP NOT NULL,
    segment_meters  DOUBLE PRECISION,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(8),
    modified_at     TIMESTAMP,
    modified_by     VARCHAR(8)
    );

CREATE INDEX IF NOT EXISTS idx_cour_loc_courier_time
    ON courier_location (courier_id, event_time DESC);
