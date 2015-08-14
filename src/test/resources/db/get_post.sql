-- get_should_returns_text
INSERT INTO tag(id, name) VALUES(1001, 'tag1');
INSERT INTO tag(id, name) VALUES(1002, 'tag2');
INSERT INTO tag(id, name) VALUES(1003, 'tag3');
INSERT INTO tag(id, name) VALUES(1004, 'tag4');

INSERT INTO post (id, blog_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1000, 1000, 'sample title', 'sample slug', '<div>This is the content</div>', 1, 1, 0, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1004, 1);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1003, 2);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1002, 3);

-- get_should_returns_404_for_deleted_post
INSERT INTO post (id, blog_id, type, status, deleted, created, updated, published)
  VALUES(9998, 1000, 1, 1, 1, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');
