-- should_returns_text
INSERT INTO tag(id, name) VALUES(1001, 'tag1');
INSERT INTO tag(id, name) VALUES(1002, 'tag2');
INSERT INTO tag(id, name) VALUES(1003, 'tag3');
INSERT INTO tag(id, name) VALUES(1004, 'tag4');

INSERT INTO post (id, blog_id, user_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1000, 100, 101, 'sample title', 'sample slug', '<div>This is the content</div>', 1, 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1004, 1);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1003, 2);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1002, 3);

INSERT INTO post_entry(post_fk, blog_id, posted) VALUE (1000, 100, now());

-- should_returns_404_for_deleted_post
INSERT INTO post (id, blog_id, user_id, type, status, deleted, created, updated, published)
  VALUES(9998, 9998, 9998, 1, 1, true, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUE (9998, 9998, now());
