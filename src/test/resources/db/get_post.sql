-- should_returns_text
INSERT INTO tag(id, name) VALUES(1001, 'tag1');
INSERT INTO tag(id, name) VALUES(1002, 'tag2');
INSERT INTO tag(id, name) VALUES(1003, 'tag3');
INSERT INTO tag(id, name) VALUES(1004, 'tag4');

INSERT INTO post (id, blog_id, user_id, title, slug, content, status, deleted, created, updated, published)
  VALUES(1000, 100, 101, 'sample title', 'sample slug', '<div>This is the content</div>', 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1004, 1);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1003, 2);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1000, 1002, 3);

INSERT INTO post_entry(post_fk, blog_id, posted) VALUE (1000, 100, now());

INSERT INTO attachment(id, xid, post_fk, name, description, content_type, content_length, duration_seconds, deleted, thumbnail_url, created)
    VALUES(1100, '2309sdkjl', 1000, 'video1', 'this is a video', 'video/quick-time', 143043, 30, false, 'http://www.img.com/1100_thumb.png', now());

INSERT INTO attachment(id, post_fk, name, description, content_type, content_length, width, height, deleted, url, thumbnail_url, created)
  VALUES(1101, 1000, 'image1', 'this is an image', 'image/png', 430394, 120, 144, false, 'http://www.img.com/1101.png', 'http://www.img.com/1101_thumb.png', now());

-- should_returns_404_for_deleted_post
INSERT INTO post (id, blog_id, user_id, status, deleted, created, updated, published)
  VALUES(9998, 9998, 9998, 1, true, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUE (9998, 9998, now());
