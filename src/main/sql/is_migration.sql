-- clean
delete from post_tag;
delete from post_entry;
delete from post_attachment;

delete from attachment;
delete from post;
delete from tag;

-- posts
INSERT INTO post(
  id,
  blog_id,
  user_id,
  status,
  created,
  updated,
  deleted
)
  SELECT
    node_id,
    node_channel_fk,
    node_owner_fk,
    1,
    node_date,
    node_date,
    node_deleted
  FROM is5.node
  WHERE node_type_fk=1
;

UPDATE post JOIN is5.nattr ON id=nattr_node_fk SET title=nattr_value WHERE nattr_name='title';
UPDATE post JOIN is5.nattr ON id=nattr_node_fk SET content=nattr_value WHERE nattr_name='content';


-- post_entry
INSERT INTO post_entry(
  post_fk,
  blog_id
)
  SELECT nprel_node_fk, nprel_party_fk
  FROM is5.nprel
  WHERE nprel_type_fk=1;



-- Attachments
INSERT INTO attachment (
  id,
  created,
  deleted
)
  SELECT DISTINCT
    A.node_id,
    A.node_date,
    A.node_deleted
  FROM
    is5.node A
    JOIN is5.nrel R ON A.node_id=R.nrel_source_fk
    JOIN post P on P.id=R.nrel_dest_fk
  WHERE
    A.node_type_fk=100
    AND R.nrel_type_fk=100;

UPDATE attachment
  JOIN is5.nattr ON id=nattr_node_fk
  SET name=nattr_value
  WHERE nattr_name='name';

UPDATE attachment
  JOIN is5.nattr ON id=nattr_node_fk
SET description=nattr_value
WHERE nattr_name='description';

UPDATE attachment
  JOIN is5.nattr ON id=nattr_node_fk
SET content_length=nattr_value
WHERE nattr_name='size';

UPDATE attachment
  JOIN is5.nattr ON id=nattr_node_fk
SET url=nattr_value
WHERE nattr_name='url';

UPDATE attachment
  JOIN is5.nattr ON id=nattr_node_fk
SET thumbnail_url=nattr_value
WHERE nattr_name='thumbnail_url';

UPDATE attachment
  JOIN is5.nattr ON id=nattr_node_fk
SET oembed=true
WHERE nattr_name='oembed';

UPDATE attachment SET content_type='image/jpg' WHERE LOWER(url) LIKE '%jpg';
UPDATE attachment SET content_type='image/png' WHERE LOWER(url) LIKE '%png';
UPDATE attachment SET content_type='image/gif' WHERE LOWER(url) LIKE '%gif';
UPDATE attachment SET content_type='video/mp4' WHERE LOWER(url) LIKE '%mp4';
UPDATE attachment SET content_type='video/x-flx' WHERE LOWER(url) LIKE '%flv';
UPDATE attachment SET content_type='video/quicktime' WHERE LOWER(url) LIKE '%mov';
UPDATE attachment SET content_type='video/x-msvideo' WHERE LOWER(url) LIKE '%avi';
UPDATE attachment SET content_type='video/x-ms-wmv'  WHERE LOWER(url) LIKE '%wmv';

INSERT INTO post_attachment (
  attachment_fk,
  post_fk,
  rank
)
  SELECT DISTINCT
    A.id,
    R.nrel_dest_fk,
    R.nrel_rank
  FROM
    attachment A
    JOIN is5.nrel R ON A.id=R.nrel_source_fk
    JOIN post P ON R.nrel_dest_fk=P.id
  WHERE R.nrel_type_fk=100;
