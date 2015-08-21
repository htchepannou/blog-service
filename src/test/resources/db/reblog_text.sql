-- should_reblog_text
INSERT INTO post (id, blog_id, user_id, title, type, status, deleted, created, updated, published)
VALUES(1000, 100, 101, 'hello world', 1, 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(100, 1000, now());


--  should_return_404_when_deleted
INSERT INTO post (id, blog_id, user_id, title, type, status, deleted, created, updated, published)
VALUES(4000, 400, 400, 'hello world', 1, 1, true, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(400, 4000, now());
