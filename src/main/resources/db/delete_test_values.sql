
DELETE FROM favorite_albums
WHERE (album_id, user_id)
IN (
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10)
);

DELETE FROM post_album
WHERE (post_id, album_id)
IN (
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10)
);

DELETE FROM album_selected_users
WHERE (album_id, selected_user_id)
IN (
    (1, 1),
    (1, 2),
    (4, 4),
    (4, 5),
    (6, 6),
    (6, 7)
);

DELETE FROM post_ad
WHERE (post_id, buyer_id)
IN (
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10)
);

DELETE FROM likes
WHERE (post_id, comment_id, user_id)
IN (
    (1, 1, 1),
    (1, 2, 2),
    (2, 3, 3),
    (2, 4, 4),
    (3, 5, 5),
    (3, 6, 6),
    (4, 7, 7),
    (4, 8, 8),
    (5, 9, 9),
    (5, 10, 10)
);


DELETE FROM resource
WHERE (key, post_id)
IN (
    ('key1', 1),
    ('key2', 2),
    ('key3', 3),
    ('key4', 4),
    ('key5', 5),
    ('key6', 6),
    ('key7', 7),
    ('key8', 8),
    ('key9', 9),
    ('key10', 10)
);

DELETE FROM comment
WHERE (content, author_id, post_id)
IN (
    ('Comment 1 on post 1', 1, 1),
    ('Comment 2 on post 1', 2, 1),
    ('Comment 1 on post 2', 3, 2),
    ('Comment 2 on post 2', 4, 2),
    ('Comment 1 on post 3', 5, 3),
    ('Comment 2 on post 3', 6, 3),
    ('Comment 1 on post 4', 7, 4),
    ('Comment 2 on post 4', 8, 4),
    ('Comment 1 on post 5', 9, 5),
    ('Comment 2 on post 5', 10, 5)
);

DELETE FROM album
WHERE (title, author_id)
IN (
    ('Album 1', 1),
    ('Album 2', 2),
    ('Album 3', 3),
    ('Album 4', 4),
    ('Album 5', 5),
    ('Album 6', 6),
    ('Album 7', 7),
    ('Album 8', 8),
    ('Album 9', 9),
    ('Album 10', 10)
);

DELETE FROM post
WHERE (content)
IN (
    ('Content of post 1'),
    ('Content of post 2'),
    ('Content of post 3'),
    ('Content of post 4'),
    ('Content of post 5'),
    ('Content of post 6'),
    ('Content of post 7'),
    ('Content of post 8'),
    ('Content of post 9'),
    ('Content of post 10')
);

DELETE FROM resource
WHERE id
IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);



