CREATE TABLE post_ad (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint NOT NULL,
    buyer_id bigint NOT NULL,
    appearances_left int NOT NULL,
    start_date timestamptz NOT NULL DEFAULT current_timestamp,
    end_date timestamptz NOT NULL,

    CONSTRAINT fk_post_ad_id FOREIGN KEY (post_id) REFERENCES post (id)
);