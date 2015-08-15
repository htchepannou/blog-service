package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.domain.Post;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        final String sql = "SELECT * FROM post WHERE id=? AND deleted=?";
        return template.queryForObject(
                sql,
                new Object[]{id, false},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public List<Post> findByBlog(long blogId, int limit, int offset) {
        final String sql = "SELECT P.*"
                + " FROM post P JOIN post_entry E ON P.id=E.post_fk"
                + " WHERE E.blog_id=? AND P.deleted=?"
                + " ORDER BY P.updated DESC"
                + " LIMIT ? OFFSET ?";
        return template.query(
                sql,
                new Object[] {blogId, false, limit, offset},
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
