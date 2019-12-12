CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    secret VARCHAR(255) NOT NULL,
    contacts TEXT,
    role VARCHAR(255) NOT NULL,
    timezone VARCHAR(255) DEFAULT 'UTC' NOT NULL
)
