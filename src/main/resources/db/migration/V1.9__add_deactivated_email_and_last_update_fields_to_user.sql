ALTER TABLE users
    ADD deactivated_email VARCHAR(255) DEFAULT NULL;

ALTER TABLE users
    ADD last_update TIMESTAMP DEFAULT now();
