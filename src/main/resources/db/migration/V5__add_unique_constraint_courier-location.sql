ALTER TABLE courier_location
    ADD CONSTRAINT uq_courier_location_courier_time
        UNIQUE (courier_id, event_time);