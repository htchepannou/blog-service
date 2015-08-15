package com.tchepannou.blog.dao.jdbc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Tag;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcTagDao implements TagDao{
    //-- Private
    private JdbcTemplate template;


    //-- Constructor
    public JdbcTagDao(DataSource ds){
        this.template = new JdbcTemplate(ds);
    }

    //-- PostDao overrides
    @Override
    public List<Tag> findByPost(long id) {
        final String sql = "SELECT T.*"
                + " FROM tag T JOIN post_tag P ON T.id=P.tag_fk"
                + " WHERE P.post_fk=?"
                + " ORDER BY P.rank;";

        return template.query(
                sql,
                new Object[]{id},
                (rs, i) -> mapTag(rs)
        );
    }

    @Override
    public Multimap<Long, Tag> findByPosts(Collection<Long> postIds) {
        final List<PostTag> postTags = findPostTagsByPosts(postIds);

        final List<Long> tagIds = postTags.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toList());

        final List<Tag> tags = findTagsByIds(tagIds);

        final Map<Long, Tag> tagMap = tags.stream()
                .collect(Collectors.toMap(i -> i.getId(), i -> i));

        final Multimap<Long, Tag> result = LinkedListMultimap.create();
        postTags.forEach(
                postTag -> result.put(postTag.getPostId(), tagMap.get(postTag.getTagId()))
        );

        return result;
    }



    //-- Private
    private List<PostTag> findPostTagsByPosts(Collection<Long> postIds) {
        if (postIds.isEmpty()){
            return Collections.emptyList();
        }

        final String sql = "SELECT * FROM post_tag WHERE post_fk IN (" + toParamVars(postIds) + ") ORDER BY rank";

        return template.query(
                sql,
                postIds.toArray(),
                (rs, i) -> mapPostTag(rs)
        );
    }

    private List<Tag> findTagsByIds(Collection<Long> tagIds) {
        if (tagIds.isEmpty()){
            return Collections.emptyList();
        }

        final String sql = "SELECT * FROM tag WHERE id IN (" + toParamVars(tagIds) + ")";

        return template.query(
                sql,
                tagIds.toArray(),
                (rs, i) -> mapTag(rs)
        );
    }

    private String toParamVars(Collection items){
        StringBuilder params = new StringBuilder();
        items.stream().forEach(post -> {
            if (params.length()>0){
                params.append(',');
            }
            params.append('?');
        });
        return params.toString();
    }

    private Tag mapTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag ();

        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));

        return tag;
    }

    private PostTag mapPostTag(ResultSet rs) throws SQLException{
        PostTag tag = new PostTag();
        tag.setTagId(rs.getLong("tag_fk"));
        tag.setPostId(rs.getLong("post_fk"));
        return tag;
    }


    //-- Inner classes
    public static class PostTag{
        private long tagId;
        private long postId;

        public long getTagId() {
            return tagId;
        }

        public void setTagId(long tagId) {
            this.tagId = tagId;
        }

        public long getPostId() {
            return postId;
        }

        public void setPostId(long postId) {
            this.postId = postId;
        }
    }
}
