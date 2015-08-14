CREATE TABLE tag(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE post(
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  blog_id BIGINT NOT NULL,
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

  PRIMARY KEY(post_fk, blog_id),
  CONSTRAINT fk_post_entry__post_fk FOREIGN KEY (post_fk) REFERENCES post(id)
);
