CREATE TABLE IF NOT EXISTS store (
                                     id              BIGSERIAL PRIMARY KEY,
                                     name            VARCHAR(150) NOT NULL,
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(8),
    modified_at     TIMESTAMP,
    modified_by     VARCHAR(8),
    CONSTRAINT chk_store_latitude  CHECK (latitude  BETWEEN -90  AND 90),
    CONSTRAINT chk_store_longitude CHECK (longitude BETWEEN -180 AND 180)
    );

CREATE INDEX IF NOT EXISTS idx_store_name     ON store (name);
CREATE INDEX IF NOT EXISTS idx_store_lat_lng  ON store (latitude, longitude);