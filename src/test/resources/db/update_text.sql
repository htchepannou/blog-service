-- should_update_text
INSERT INTO tag(id, name) VALUES(1001, 'tag1');

INSERT INTO post (id, blog_id, user_id, title, type, status, deleted, created, updated, published)
VALUES(1000, 100, 101, 'hello world', 1, 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1001, 1);

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(100, 1000, now());


-- should_return_403_when_now_owner_of_blog
INSERT INTO post (id, blog_id, user_id, title, slug, content, type, status, deleted, created, updated, published)
VALUES(2000, 200, 200, 'sample title', 'sample slug', '<div>This is the content</div>', 1, 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(200, 2000, now());
INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(100, 2000, now());

-- should_return_403_when_bad_permission 
INSERT INTO post (id, blog_id, user_id, title, type, status, deleted, created, updated, published)
VALUES(3000, 300, 300, 'hello world', 1, 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(300, 3000, now());

--  should_return_404_when_deleted
INSERT INTO post (id, blog_id, user_id, title, type, status, deleted, created, updated, published)
VALUES(4000, 400, 400, 'hello world', 1, 1, true, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(400, 4000, now());
