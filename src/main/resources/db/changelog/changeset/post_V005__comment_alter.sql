ALTER TABLE comment
    ADD COLUMN verified      BOOLEAN,
    ADD COLUMN verified_date TIMESTAMP;