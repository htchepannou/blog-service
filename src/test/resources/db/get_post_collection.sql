-- should_returns_list
INSERT INTO post (id, blog_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1000, 100, 'title1000', 'slug1000', '<div>content1000</div>', 1, 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1001, 100, 'title1001', 'slug1001', '<div>content1001</div>', 1, 1, false, '2015-01-10 03:14:07', '2038-01-13 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1002, 100, 'title1002', 'slug1002', '<div>content1002</div>', 2, 0, false, '2015-01-10 03:14:07', '2038-01-14 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1003, 100, 'title1003', 'slug1003', '<div>content1003</div>', 2, 0, true, '2015-01-10 03:14:07', '2038-01-15 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, title, slug, content, type, status, deleted, created, updated, published)
  VALUES(1011, 101, 'title1011', 'slug1011', '<div>content1011</div>', 2, 0, false, '2015-01-10 03:14:07', '2038-01-16 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1000, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1002, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1003, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1011, 100, '2015-01-10 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1000, 101, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1011, 101, '2015-01-10 03:14:07');

-- should_returns_list_with_limit_offset
INSERT INTO post (id, blog_id, type, status, deleted, created, updated)
  VALUES(2000, 200, 1, 1, false, '2015-01-20 03:14:07', '2038-01-12 03:14:07');

INSERT INTO post (id, blog_id, type, status, deleted, created, updated)
  VALUES(2001, 200, 1, 1, false, '2015-01-21 03:14:07', '2038-01-13 03:14:07');

INSERT INTO post (id, blog_id, type, status, deleted, created, updated)
  VALUES(2002, 200, 1, 1, false, '2015-01-22 03:14:07', '2038-01-14 03:14:07');

INSERT INTO post (id, blog_id, type, status, deleted, created, updated)
  VALUES(2003, 200, 1, 1, false, '2015-01-23 03:14:07', '2038-01-15 03:14:07');

INSERT INTO post (id, blog_id, type, status, deleted, created, updated)
  VALUES(2004, 200, 1, 1, false, '2015-01-24 03:14:07', '2038-01-16 03:14:07');

INSERT INTO post (id, blog_id, type, status, deleted, created, updated)
  VALUES(2005, 200, 1, 1, false, '2015-01-25 03:14:07', '2038-01-17 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(2001, 200, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(2002, 200, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(2003, 200, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(2004, 200, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(2005, 200, '2015-01-10 03:14:07');
