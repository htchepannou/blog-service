-- clean
delete from event_log;
delete from post_tag;
delete from post_entry;

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


-- Attachments
INSERT INTO attachment (
  id,
  post_fk,
  created,
  deleted
)
  SELECT DISTINCT
    R.nrel_id,
    P.id,
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
  JOIN is5.nrel ON id=nrel_id
  JOIN is5.nattr ON nrel_source_fk=nattr_node_fk
  SET name=nattr_value
  WHERE nattr_name='name';

UPDATE attachment
  JOIN is5.nrel ON id=nrel_id
  JOIN is5.nattr ON nrel_source_fk=nattr_node_fk
SET description=nattr_value
WHERE nattr_name='description';

UPDATE attachment
  JOIN is5.nrel ON id=nrel_id
  JOIN is5.nattr ON nrel_source_fk=nattr_node_fk
SET content_length=nattr_value
WHERE nattr_name='size';

UPDATE attachment
  JOIN is5.nrel ON id=nrel_id
  JOIN is5.nattr ON nrel_source_fk=nattr_node_fk
SET url=nattr_value
WHERE nattr_name='url';

UPDATE attachment
  JOIN is5.nrel ON id=nrel_id
  JOIN is5.nattr ON nrel_source_fk=nattr_node_fk
SET thumbnail_url=nattr_value
WHERE nattr_name='thumbnail_url';

UPDATE attachment
  JOIN is5.nrel ON id=nrel_id
  JOIN is5.nattr ON nrel_source_fk=nattr_node_fk
SET oembed=true, url=nattr_value
WHERE nattr_name='oembed';


-- post_entry
INSERT INTO post_entry(
  post_fk,
  blog_id
)
  SELECT nprel_node_fk, nprel_party_fk
  FROM is5.nprel
  WHERE nprel_type_fk=1;
