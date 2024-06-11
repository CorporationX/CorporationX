CREATE TABLE resource (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    key VARCHAR(50) NOT NULL,
    size BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    name VARCHAR(150),
    type VARCHAR(150),
    post_id BIGINT NOT NULL,

    FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE UNIQUE INDEX resource_key_idx ON resource (key);
