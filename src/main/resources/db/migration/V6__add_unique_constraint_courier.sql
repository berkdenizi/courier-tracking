CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS ux_courier_identity_number_idx
    ON courier (identity_number);

CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS ux_courier_phone_number_idx
    ON courier (phone_number);