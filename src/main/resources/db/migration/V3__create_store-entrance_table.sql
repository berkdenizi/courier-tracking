CREATE TABLE IF NOT EXISTS store_entrance (
    id                BIGSERIAL PRIMARY KEY,
    store_id          BIGINT NOT NULL REFERENCES store (id),
    courier_id        BIGINT NOT NULL,
    entrance_time     TIMESTAMP NOT NULL,
    distance_meters   DOUBLE PRECISION,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(8),
    modified_at       TIMESTAMP,
    modified_by       VARCHAR(8)
    );