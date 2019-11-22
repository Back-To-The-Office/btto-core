CREATE TABLE department (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    owner INTEGER REFERENCES users(id) NOT NULL
)
