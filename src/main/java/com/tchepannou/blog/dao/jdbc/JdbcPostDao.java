package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.domain.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import static com.tchepannou.blog.dao.jdbc.JdbcUtils.toTimestamp;

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

        final Timestamp now = new Timestamp(System.currentTimeMillis());
        post.setCreated(now);
        post.setUpdated(now);

        new JdbcTemplate(dataSource).update(cnn -> insertPreparedStatement(post, cnn), keyHolder);

        post.setId(keyHolder.getKey().longValue());
    }

    //-- Private
    public PreparedStatement insertPreparedStatement(Post post, Connection connection) throws SQLException {
        final String sql = "INSERT INTO post"
                + "(blog_id, user_id, type, status, title, content, slug, created, updated, published, deleted)"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setLong(1, post.getBlogId());
        ps.setLong(2, post.getUserId());
        ps.setInt(3, post.getType().value());
        ps.setInt(4, post.getStatus().value());
        ps.setString(5, post.getTitle());
        ps.setString(6, post.getContent());
        ps.setString(7, post.getSlug());
        ps.setTimestamp(8, toTimestamp(post.getCreated()));
        ps.setTimestamp(9, toTimestamp(post.getUpdated()));
        ps.setTimestamp(10, toTimestamp(post.getPublished()));
        ps.setBoolean(11, false);

        return ps;
    }

    private Post map(ResultSet rs) throws SQLException {
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
