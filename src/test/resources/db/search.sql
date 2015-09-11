-- should_returns_list
INSERT INTO post (id, blog_id, user_id, title, slug, content, status, deleted, created, updated, published)
  VALUES(1000, 100, 101, 'title1000', 'slug1000', '<div>content1000</div>', 1, false, '2015-01-10 03:14:07', '2038-01-12 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, user_id, title, slug, content, status, deleted, created, updated, published)
  VALUES(1001, 100, 101, 'title1001', 'slug1001', '<div>content1001</div>', 1, false, '2015-01-10 03:14:07', '2038-01-13 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, user_id, title, slug, content, status, deleted, created, updated, published)
  VALUES(1002, 100, 101, 'title1002', 'slug1002', '<div>content1002</div>', 0, false, '2015-01-10 03:14:07', '2038-01-14 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, user_id, title, slug, content, status, deleted, created, updated, published)
  VALUES(1003, 100, 101, 'title1003', 'slug1003', '<div>content1003</div>', 0, true, '2015-01-10 03:14:07', '2038-01-15 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post (id, blog_id, user_id, title, slug, content, status, deleted, created, updated, published)
  VALUES(1011, 101, 102, 'title1011', 'slug1011', '<div>content1011</div>', 0, false, '2015-01-10 03:14:07', '2038-01-16 03:14:07', '2038-01-19 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1000, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1001, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1002, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1003, 100, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1011, 100, '2015-01-10 03:14:07');

INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1000, 101, '2015-01-10 03:14:07');
INSERT INTO post_entry(post_fk, blog_id, posted) VALUES(1011, 101, '2015-01-10 03:14:07');

INSERT INTO tag(id, name) VALUES(1001, 'tag1');
INSERT INTO tag(id, name) VALUES(1002, 'tag2');
INSERT INTO tag(id, name) VALUES(1003, 'tag3');
INSERT INTO tag(id, name) VALUES(1004, 'tag4');

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1011, 1001, 1);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1011, 1002, 2);

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1002, 1002, 1);
INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1002, 1003, 2);

INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(1001, 1004, 1);


INSERT INTO attachment(id, name, description, content_type, content_length, width, height, deleted, url, thumbnail_url, created)
VALUES(1100, 'image1', 'this is an image', 'video/quicktime', 430394, 120, 144, false, 'http://www.img.com/1101.png', 'http://www.img.com/1101_thumb.png', now());

INSERT INTO attachment(id, name, description, content_type, content_length, width, height, deleted, url, thumbnail_url, created)
VALUES(1200, 'image2', 'this is an image', 'image/png', 430394, 120, 144, false, 'http://www.img.com/1101.png', 'http://www.img.com/1101_thumb.png', now());

INSERT INTO attachment(id, name, description, content_type, content_length, width, height, deleted, url, thumbnail_url, created)
VALUES(1101, 'file1', 'this is an file', 'text/plain', 430394, 120, 144, false, 'http://www.img.com/1101.png', 'http://www.img.com/1101_thumb.png', now());

INSERT INTO attachment(id, name, description, content_type, content_length, width, height, deleted, url, thumbnail_url, created)
VALUES(1201, 'file1201', 'this is an file', 'text/plain', 430394, 120, 144, false, 'http://www.img.com/1101.txt', 'http://www.img.com/1101_thumb.png', now());

INSERT INTO attachment(id, name, description, content_type, content_length, width, height, deleted, url, thumbnail_url, created)
VALUES(1202, 'file1202', 'this is an file', 'text/xml', 430394, 120, 144, false, 'http://www.img.com/1101.xml', 'http://www.img.com/1101_thumb.png', now());

INSERT INTO post_attachment(post_fk, attachment_fk, rank) VALUES(1000, 1100, 1);
INSERT INTO post_attachment(post_fk, attachment_fk, rank) VALUES(1000, 1200, 2);
INSERT INTO post_attachment(post_fk, attachment_fk, rank) VALUES(1001, 1101, 1);
INSERT INTO post_attachment(post_fk, attachment_fk, rank) VALUES(1002, 1201, 1);
INSERT INTO post_attachment(post_fk, attachment_fk, rank) VALUES(1002, 1202, 2);

