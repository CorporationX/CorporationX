CREATE TABLE post (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content varchar(4096) NOT NULL,
    author_id bigint,
    project_id bigint,
    published boolean DEFAULT false NOT NULL,
    published_at timestamptz,
    deleted boolean DEFAULT false NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);