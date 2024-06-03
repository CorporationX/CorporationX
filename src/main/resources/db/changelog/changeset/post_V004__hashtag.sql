CREATE TABLE hashtag (
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hashtag    VARCHAR(255) NOT NULL,
    post_id    BIGINT NOT NULL ,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_hashtag_post FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE INDEX idx_hashtag_hashtag ON hashtag (hashtag);