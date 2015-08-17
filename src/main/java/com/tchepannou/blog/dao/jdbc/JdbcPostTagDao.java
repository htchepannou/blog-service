package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.domain.PostTag;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.tchepannou.blog.dao.jdbc.JdbcUtils.toParamVars;

public class JdbcPostTagDao implements PostTagDao {
    //-- Private
    private DataSource dataSource;


    //-- Constructor
    public JdbcPostTagDao(DataSource ds){
        this.dataSource = ds;
    }

    //-- PostTagDao overrides
    @Override
    public List<PostTag> findByPost(long postId) {
        final String sql = "SELECT * FROM post_tag WHERE post_fk=? ORDER BY rank";

        return new JdbcTemplate(dataSource).query(
                sql,
                new Object[] {postId},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public List<PostTag> findByPosts(Collection<Long> postIds) {
        if (postIds.isEmpty()){
            return Collections.emptyList();
        }

        final String sql = "SELECT * FROM post_tag WHERE post_fk IN (" + toParamVars(postIds) + ") ORDER BY rank";

        return new JdbcTemplate(dataSource).query(
                sql,
                postIds.toArray(),
                (rs, i) -> map(rs)
        );
    }

    @Override
    public void deleteByPost(long postId) {
        final String sql = "DELETE FROM post_tag WHERE post_fk=?";
        new JdbcTemplate(dataSource).update(
                sql,
                postId
        );
    }

    @Override
    public void add(long postId, List<Long> tagIds) {
        if (tagIds.isEmpty()){
            return;
        }

        List<Object[]> params = new ArrayList<>();
        for (int i=0 ; i<tagIds.size() ; i++){
            params.add(new Object[]{postId, tagIds.get(i), i});
        }

        final String sql = "INSERT INTO post_tag(post_fk, tag_fk, rank) VALUES(?,?,?)";
        new JdbcTemplate(dataSource).batchUpdate(sql, params);
    }

    //-- Private
    private PostTag map(ResultSet rs) throws SQLException {
        PostTag tag = new PostTag();
        tag.setTagId(rs.getLong("tag_fk"));
        tag.setPostId(rs.getLong("post_fk"));
        return tag;
    }

}
