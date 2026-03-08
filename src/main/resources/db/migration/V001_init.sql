CREATE TABLE app_info (
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes        TEXT
);