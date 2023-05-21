CREATE TABLE contact_preferences (
    id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id bigint NOT NULL,
    preference smallint NOT NULL,

    CONSTRAINT fk_contact_preferences_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);