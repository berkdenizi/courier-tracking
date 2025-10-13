CREATE TABLE IF NOT EXISTS courier
(
    id              bigserial primary key,
    identity_number varchar(12)             not null unique,
    phone_number    varchar(15)             not null unique,
    name            varchar(120)            not null,
    surname         varchar(120)            not null,
    created_at      timestamp default now() not null,
    created_by      varchar(8),
    modified_at     timestamp,
    modified_by     varchar(8)
);

