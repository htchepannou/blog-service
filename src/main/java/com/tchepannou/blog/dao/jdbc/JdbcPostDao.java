package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.domain.Post;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcPostDao implements PostDao{
    //-- Private
    private JdbcTemplate template;


    //-- Constructor
    public JdbcPostDao (DataSource ds){
        this.template = new JdbcTemplate(ds);
    }

    //-- PostDao overrides
    @Override
    public Post findById(long id) {
        return template.queryForObject(
                "SELECT * FROM post WHERE id=? AND deleted=?",
                new Object[]{id, false},
                (rs, i) -> map(rs)
        );
    }


    //-- Private
    public Post map(ResultSet rs) throws SQLException {
        Post post = new Post ();

        post.setBlogId(rs.getLong("blog_id"));
        post.setContent(rs.getString("content"));
        post.setCreated(rs.getTimestamp("created"));
        post.setDeleted(rs.getBoolean("deleted"));
        post.setId(rs.getLong("id"));
        post.setPublished(rs.getTimestamp("published"));
        post.setSlug(rs.getString("slug"));
        post.setStatus(Post.Status.fromValue(rs.getInt("status")));
        post.setTitle(rs.getString("title"));
        post.setType(Post.Type.fromValue(rs.getInt("type")));
        post.setUpdated(rs.getTimestamp("updated"));

        return post;
    }
}
