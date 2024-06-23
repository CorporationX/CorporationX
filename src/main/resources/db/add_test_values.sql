INSERT INTO post (content, author_id, project_id, published, published_at, scheduled_at, deleted)
VALUES ('Content of post 1', NULL, 1, false, NULL, NULL, false),
       ('Content of post 2', 2, NULL, false, NULL, NULL, false),
       ('Content of post 3', 3, NULL, false, NULL, NULL, false),
       ('Content of post 4', NULL, 2, false, NULL, NULL, false),
       ('Content of post 5', 5, NULL, false, NULL, NULL, false),
       ('Content of post 6', NULL, 3, false, NULL, NULL, false),
       ('Content of post 7', NULL, 4, false, NULL, NULL, false),
       ('Content of post 8', 8, NULL, false, NULL, NULL, false),
       ('Content of post 9', NULL, 5, false, NULL, NULL, false),
       ('Content of post 10', 10, NULL, false, NULL, NULL, false);

INSERT INTO comment (content, author_id, post_id)
VALUES ('Comment 1 on post 1', 1, 1),
       ('Comment 2 on post 1', 2, 1),
       ('Comment 1 on post 2', 3, 2),
       ('Comment 2 on post 2', 4, 2),
       ('Comment 1 on post 3', 5, 3),
       ('Comment 2 on post 3', 6, 3),
       ('Comment 1 on post 4', 7, 4),
       ('Comment 2 on post 4', 8, 4),
       ('Comment 1 on post 5', 9, 5),
       ('Comment 2 on post 5', 10, 5);

INSERT INTO likes (post_id, comment_id, user_id)
VALUES (1, 1, 1),
       (1, 2, 2),
       (2, 3, 3),
       (2, 4, 4),
       (3, 5, 5),
       (3, 6, 6),
       (4, 7, 7),
       (4, 8, 8),
       (5, 9, 9),
       (5, 10, 10);

INSERT INTO album (title, description, author_id, visibility)
VALUES ('Album 1', 'Description of album 1', 1, 'SELECTED_USERS'),
       ('Album 2', 'Description of album 2', 2, 'ALL_USERS'),
       ('Album 3', 'Description of album 3', 3, 'ALL_USERS'),
       ('Album 4', 'Description of album 4', 4, 'SELECTED_USERS'),
       ('Album 5', 'Description of album 5', 5, 'ALL_USERS'),
       ('Album 6', 'Description of album 6', 6, 'SELECTED_USERS'),
       ('Album 7', 'Description of album 7', 7, 'ONLY_SUBSCRIBERS'),
       ('Album 8', 'Description of album 8', 8, 'ALL_USERS'),
       ('Album 9', 'Description of album 9', 9, 'ONLY_AUTHOR'),
       ('Album 10', 'Description of album 10', 10, 'ONLY_AUTHOR');

INSERT INTO post_album (post_id, album_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9),
       (10, 10);

INSERT INTO favorite_albums (album_id, user_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9),
       (10, 10);

INSERT INTO post_ad (post_id, buyer_id, appearances_left, start_date, end_date)
VALUES
    (1, 1, 10, current_timestamp, current_timestamp + interval '30 days'),
    (2, 2, 15, current_timestamp, current_timestamp + interval '30 days'),
    (3, 3, 20, current_timestamp, current_timestamp + interval '30 days'),
    (4, 4, 25, current_timestamp, current_timestamp + interval '30 days'),
    (5, 5, 30, current_timestamp, current_timestamp + interval '30 days'),
    (6, 6, 35, current_timestamp, current_timestamp + interval '30 days'),
    (7, 7, 40, current_timestamp, current_timestamp + interval '30 days'),
    (8, 8, 45, current_timestamp, current_timestamp + interval '30 days'),
    (9, 9, 50, current_timestamp, current_timestamp + interval '30 days'),
    (10, 10, 55, current_timestamp, current_timestamp + interval '30 days');

INSERT INTO album_selected_users (album_id, selected_user_id)
VALUES
    (1, 1),
    (1, 2),
    (4, 4),
    (4, 5),
    (6, 6),
    (6, 7);

INSERT INTO resource (key, size, name, type, post_id)
VALUES
    ('key1', 1024, 'Resource 1', 'Type 1', 1),
    ('key2', 2048, 'Resource 2', 'Type 2', 2),
    ('key3', 4096, 'Resource 3', 'Type 3', 3),
    ('key4', 8192, 'Resource 4', 'Type 4', 4),
    ('key5', 16384, 'Resource 5', 'Type 5', 5),
    ('key6', 32768, 'Resource 6', 'Type 6', 6),
    ('key7', 65536, 'Resource 7', 'Type 7', 7),
    ('key8', 131072, 'Resource 8', 'Type 8', 8),
    ('key9', 262144, 'Resource 9', 'Type 9', 9),
    ('key10', 524288, 'Resource 10', 'Type 10', 10);
