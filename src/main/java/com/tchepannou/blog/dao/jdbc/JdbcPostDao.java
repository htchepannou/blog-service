package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.domain.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class JdbcPostDao implements PostDao{
    //-- Private
    private DataSource dataSource;


    //-- Constructor
    public JdbcPostDao (DataSource ds){
        this.dataSource = ds;
    }

    //-- PostDao overrides
    @Override
    public Post findById(long id) {
        final String sql = "SELECT * FROM post WHERE id=? AND deleted=?";
        return new JdbcTemplate(dataSource).queryForObject(
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
        return new JdbcTemplate(dataSource).query(
                sql,
                new Object[]{blogId, false, limit, offset},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public void create(Post post) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        final String sql = "INSERT INTO post(blogId, userId, type, status, title, content, slug, created, updated, published)"
                + " VALUES(?,?,?,?,?,?,?,?,?,?)";

        new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {

                Timestamp now = new Timestamp(System.currentTimeMillis());
                Timestamp published = post.getPublished() != null ? new Timestamp(post.getPublished().getTime()) : null;

                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setLong(1, post.getBlogId());
                ps.setLong(2, post.getUserId());
                ps.setInt(3, post.getType().value());
                ps.setInt(4, post.getStatus().value());
                ps.setString(5, post.getTitle());
                ps.setString(6, post.getContent());
                ps.setString(7, post.getSlug());
                ps.setTimestamp(8, now);
                ps.setTimestamp(9, now);
                ps.setTimestamp(10, published);

                return ps;
            }
        }, keyHolder);

        post.setId(keyHolder.getKey().longValue());
    }

    //-- Private
    public Post map(ResultSet rs) throws SQLException {
        Post post = new Post ();

        post.setBlogId(rs.getLong("blog_id"));
        post.setUserId(rs.getLong("user_id"));
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
