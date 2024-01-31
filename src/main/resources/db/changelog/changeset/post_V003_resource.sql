CREATE TABLE resource_id (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    key VARCHAR(50) NOT NULL,
    size BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(50) NOT NULL,
    post_id BIGINT NOT NULL,

    FOREIGN KEY (post_id) REFERENCES post(id)
);