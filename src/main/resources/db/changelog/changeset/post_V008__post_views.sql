CREATE TABLE post_views
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    post_id    bigint REFERENCES post(id) NOT NULL,
    viewer_id  bigint REFERENCES users(id) NOT NULL,
    UNIQUE (post_id, viewer_id)
);
