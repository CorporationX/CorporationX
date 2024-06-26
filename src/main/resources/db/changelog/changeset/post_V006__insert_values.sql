-- Insert into post
INSERT INTO post (content, author_id, project_id, published, published_at, scheduled_at, deleted)
VALUES
    ('First post content', 1, 1, true, '2023-01-01 10:00:00', '2023-01-01 09:00:00', false),
    ('Second post content', 2, 1, false, NULL, '2023-01-02 09:00:00', false),
    ('Third post content', 1, 2, true, '2023-01-03 10:00:00', NULL, true),
    ('Fourth post content', 2, 2, false, NULL, '2023-01-04 10:00:00', false),
    ('Fifth post content', 3, 3, true, '2023-01-05 10:00:00', NULL, false),
    ('Sixth post content', 1, 3, false, NULL, '2023-01-06 10:00:00', true),
    ('Seventh post content', 2, 4, true, '2023-01-07 10:00:00', NULL, false),
    ('Eighth post content', 3, 4, false, NULL, '2023-01-08 10:00:00', true),
    ('Ninth post content', 1, 5, true, '2023-01-09 10:00:00', NULL, false),
    ('Tenth post content', 2, 5, false, NULL, '2023-01-10 10:00:00', true);

-- Insert into comment
INSERT INTO comment (content, author_id, post_id, verified, verified_date)
VALUES
    ('First comment on first post', 3, 1, true, '2023-01-01 11:00:00'),
    ('Second comment on first post', 4, 1, false, NULL),
    ('First comment on second post', 3, 2, true, '2023-01-02 11:00:00');

-- Insert into likes
INSERT INTO likes (post_id, comment_id, user_id)
VALUES
    (1, 1, 5),
    (1, 2, 6),
    (2, 3, 5);

-- Insert into album
INSERT INTO album (title, description, author_id)
VALUES
    ('First Album', 'Description for the first album', 1),
    ('Second Album', 'Description for the second album', 2);

-- Insert into post_album
INSERT INTO post_album (post_id, album_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 2);

-- Insert into favorite_albums
INSERT INTO favorite_albums (album_id, user_id)
VALUES
    (1, 1),
    (2, 2);

-- Insert into album_selected_users
INSERT INTO album_selected_users (album_id, selected_user_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 3);