CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    secret TEXT NOT NULL,
    contacts JSON,
    role TEXT NOT NULL,
    timezone TEXT DEFAULT 'UTC' NOT NULL
)
