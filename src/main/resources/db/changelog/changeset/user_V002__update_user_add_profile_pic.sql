ALTER TABLE users
ADD COLUMN if not exists profile_pic_file_id text,
ADD COLUMN if not exists profile_pic_small_file_id text;

CREATE TABLE if not exists content_data (
    id bigint PRIMARY key GENERATED ALWAYS AS IDENTITY UNIQUE,
    content oid
);