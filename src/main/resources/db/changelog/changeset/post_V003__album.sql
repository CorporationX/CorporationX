ALTER TABLE album
ADD visibility varchar(50) DEFAULT 'ALL_USERS' NOT NULL;

CREATE TABLE album_selected_users (
    album_id BIGINT NOT NULL,
    selected_user_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, selected_user_id),
    CONSTRAINT fk_album_selected_users_album FOREIGN KEY (album_id) REFERENCES album (id)
);