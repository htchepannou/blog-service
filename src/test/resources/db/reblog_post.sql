-- should_reblog_new_post
INSERT INTO post (id, blog_id, user_id, title, status, deleted, created, updated, published)
VALUES(1000, 100, 100, 'hello world', 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(100, 1000, now());


-- should_reblog_existing_post
INSERT INTO post (id, blog_id, user_id, title, status, deleted, created, updated, published)
VALUES(2000, 200, 200, 'hello world', 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(200, 2000, now());


--  should_return_404_when_deleted
INSERT INTO post (id, blog_id, user_id, title, status, deleted, created, updated, published)
VALUES(4000, 400, 400, 'hello world', 1, true, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(400, 4000, now());
