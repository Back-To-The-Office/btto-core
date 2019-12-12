CREATE TABLE participant (
    id SERIAL PRIMARY KEY,
    participant INTEGER REFERENCES users(id) NOT NULL,
    department INTEGER REFERENCES department(id) NOT NULL
)
