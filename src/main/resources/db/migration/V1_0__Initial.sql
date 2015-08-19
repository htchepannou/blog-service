CREATE TABLE tag(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE post(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  blog_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status INTEGER,
  type INTEGER,
  title VARCHAR(255),
  slug VARCHAR(255),
  content TEXT,
  created DATETIME,
  updated DATETIME,
  published DATETIME,
  deleted BIT
);
CREATE INDEX idx_post__updated ON post (updated);
CREATE INDEX idx_post__user_id ON post (user_id);

CREATE TABLE post_tag(
  post_fk BIGINT,
  tag_fk BIGINT,
  rank INTEGER,

  CONSTRAINT fk_post_tag__post_fk FOREIGN KEY (post_fk) REFERENCES post(id),
  CONSTRAINT fk_post_tag__tag_fk FOREIGN KEY (tag_fk) REFERENCES tag(id)
);

CREATE TABLE post_entry(
  post_fk BIGINT NOT NULL,
  blog_id BIGINT NOT NULL,
  posted DATETIME,

  PRIMARY KEY(post_fk, blog_id),
  CONSTRAINT fk_post_entry__post_fk FOREIGN KEY (post_fk) REFERENCES post(id)
);
CREATE INDEX idx_post_entry__posted ON post_entry (posted);

CREATE TABLE event_log(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  blog_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  post_fk BIGINT,
  created DATETIME,
  name VARCHAR(50),
  request TEXT,

  CONSTRAINT fk_event_log__post_fk FOREIGN KEY (post_fk) REFERENCES post(id)

);

CREATE INDEX idx_event_log__blog_id ON event_log (blog_id);
CREATE INDEX idx_event_log__user_id ON event_log (user_id);
CREATE INDEX idx_event_log__created ON event_log (created);

