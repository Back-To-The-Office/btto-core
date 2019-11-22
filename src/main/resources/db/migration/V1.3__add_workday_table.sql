CREATE TABLE workday (
    id SERIAL PRIMARY KEY,
    owner INTEGER REFERENCES users(id) NOT NULL,
    work_date TIMESTAMP without time zone DEFAULT now() NOT NULL,
    duration_sec BIGINT
)
