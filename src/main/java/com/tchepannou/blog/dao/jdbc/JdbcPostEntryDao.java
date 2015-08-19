package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostEntry;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class JdbcPostEntryDao implements PostEntryDao {
    private DataSource ds;

    public JdbcPostEntryDao(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public List<PostEntry> findByPost(long postId) {
        final String sql = "SELECT * FROM post_entry WHERE post_fk=?";
        return new JdbcTemplate(ds).query(
                sql,
                new Object[] {postId},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public void create(PostEntry entry) {
        entry.setPosted(new Date());
        new JdbcTemplate(ds).update(
                "INSERT INTO post_entry(blog_id, post_fk, posted) VALUES(?,?,?)",
                entry.getBlogId(),
                entry.getPostId(),
                entry.getPosted()
        );
    }

    @Override
    public void delete(Post post, long blogId) {
        new JdbcTemplate(ds).update(
                "DELETE FROM post_entry WHERE post_fk=? AND blog_id=?",
                post.getId(),
                blogId
        );
    }

    //-- Private
    private PostEntry map(ResultSet rs) throws SQLException{
        PostEntry entry = new PostEntry();
        entry.setBlogId(rs.getLong("blog_id"));
        entry.setPosted(rs.getTimestamp("posted"));
        entry.setPostId(rs.getLong("post_fk"));
        return entry;
    }
}
