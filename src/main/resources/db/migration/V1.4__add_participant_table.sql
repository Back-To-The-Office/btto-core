CREATE TABLE participants (
    participant INTEGER REFERENCES users(id) NOT NULL,
    department INTEGER REFERENCES department(id) NOT NULL
)
