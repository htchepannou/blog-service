CREATE TABLE tag(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name varchar(100) NOT NULL UNIQUE
) ENGINE=INNODB;

CREATE TABLE post(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  blog_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status INTEGER,
  title VARCHAR(255),
  slug VARCHAR(255),
  content TEXT,
  created DATETIME,
  updated DATETIME,
  published DATETIME,
  deleted BIT
) ENGINE=INNODB;
CREATE INDEX idx_post__updated ON post (updated);
CREATE INDEX idx_post__user_id ON post (user_id);

CREATE TABLE post_tag(
  post_fk BIGINT,
  tag_fk BIGINT,
  rank INTEGER,

  CONSTRAINT fk_post_tag__post_fk FOREIGN KEY (post_fk) REFERENCES post(id),
  CONSTRAINT fk_post_tag__tag_fk FOREIGN KEY (tag_fk) REFERENCES tag(id)
) ENGINE=INNODB;

CREATE TABLE post_entry(
  post_fk BIGINT NOT NULL,
  blog_id BIGINT NOT NULL,
  posted DATETIME,

  PRIMARY KEY(post_fk, blog_id),
  CONSTRAINT fk_post_entry__post_fk FOREIGN KEY (post_fk) REFERENCES post(id)
) ENGINE=INNODB;
CREATE INDEX idx_post_entry__posted ON post_entry (posted);

CREATE TABLE attachment(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  description TEXT,
  url VARCHAR(2048),
  oembed BIT,
  content_type VARCHAR(20),
  content_length BIGINT,
  thumbnail_url VARCHAR(255),
  xid VARCHAR(20),
  duration_seconds INT,
  width INT,
  height INT,
  deleted BIT,
  created DATETIME
)  ENGINE=INNODB;

CREATE TABLE post_attachment(
  post_fk BIGINT NOT NULL,
  attachment_fk BIGINT NOT NULL,
  rank INT,

  PRIMARY KEY(post_fk, attachment_fk),
  CONSTRAINT fk_post_attachment__post_fk FOREIGN KEY (post_fk) REFERENCES post(id),
  CONSTRAINT fk_post_attachment__attachment_fk FOREIGN KEY (attachment_fk) REFERENCES attachment(id)

) ENGINE=INNODB;
