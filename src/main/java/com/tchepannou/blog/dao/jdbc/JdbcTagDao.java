package com.tchepannou.blog.dao.jdbc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.PostTag;
import com.tchepannou.blog.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tchepannou.blog.dao.jdbc.JdbcUtils.toParamVars;

public class JdbcTagDao implements TagDao{
    //-- Private
    private DataSource dataSource;

    @Autowired
    private PostTagDao postTagDao;

    //-- Constructor
    public JdbcTagDao(DataSource ds){
        this.dataSource = ds;
    }

    //-- PostDao overrides
    @Override
    public List<Tag> findByPost(long id) {
        final String sql = "SELECT T.*"
                + " FROM tag T JOIN post_tag P ON T.id=P.tag_fk"
                + " WHERE P.post_fk=?"
                + " ORDER BY P.rank;";

        return new JdbcTemplate(dataSource).query(
                sql,
                new Object[]{id},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public List<Tag> findByIds(Collection<Long> ids) {
        if (ids.isEmpty()){
            return Collections.emptyList();
        }

        final String sql = "SELECT * FROM tag WHERE id IN (" + toParamVars(ids) + ")";

        return new JdbcTemplate(dataSource).query(
                sql,
                ids.toArray(),
                (rs, i) -> map(rs)
        );
    }

    @Override
    public Multimap<Long, Tag> findByPosts(Collection<Long> postIds) {
        final List<PostTag> postTags = postTagDao.findByPosts(postIds);

        final List<Long> tagIds = postTags.stream()
                .map(PostTag::getTagId)
                .collect(Collectors.toList());

        final List<Tag> tags = findByIds(tagIds);

        final Map<Long, Tag> tagMap = tags.stream()
                .collect(Collectors.toMap(i -> i.getId(), i -> i));

        final Multimap<Long, Tag> result = LinkedListMultimap.create();
        postTags.forEach(
                postTag -> result.put(postTag.getPostId(), tagMap.get(postTag.getTagId()))
        );

        return result;
    }

    @Override
    public void create(Tag tag) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        new JdbcTemplate(dataSource).update(cnn -> insertPreparedStatement(tag, cnn), keyHolder);

        tag.setId(keyHolder.getKey().longValue());
    }

    @Override
    public List<Tag> findByNames(Collection<String> names) {
        if (names.isEmpty()){
            return Collections.emptyList();
        }

        final String sql = "SELECT * FROM tag WHERE LOWER(name) IN (" + toParamVars(names) + ")";

        return new JdbcTemplate(dataSource).query(
                sql,
                names
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList())
                        .toArray(),
                (rs, i) -> map(rs)
        );
    }

    //-- Private
    private Tag map(ResultSet rs) throws SQLException {
        Tag tag = new Tag ();

        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));

        return tag;
    }

    public PreparedStatement insertPreparedStatement(Tag tag, Connection connection) throws SQLException {
        final String sql = "INSERT INTO tag"
                + "(name)"
                + " VALUES(?)";

        final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, tag.getName());

        return ps;
    }


}
