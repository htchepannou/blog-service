package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.EventLogDao;
import com.tchepannou.blog.domain.EventLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.tchepannou.blog.dao.jdbc.JdbcUtils.toTimestamp;

public class JdbcEventLogDao implements EventLogDao {
    private DataSource ds;

    public JdbcEventLogDao(DataSource ds) {
        this.ds = ds;
    }

    //-- EventLogDao overrides
    @Override
    public List<EventLog> findByPost(long postId, int limit, int offset) {
        return new JdbcTemplate(ds).query(
                "SELECT * FROM event_log WHERE post_fk=? ORDER BY created DESC LIMIT ? OFFSET ?",
                new Object[]{postId, limit, offset},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public void create(EventLog event) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        new JdbcTemplate(ds).update(cnn -> insertPreparedStatement(event, cnn), keyHolder);

        event.setId(keyHolder.getKey().longValue());
    }


    //-- Private
    private EventLog map(ResultSet rs) throws SQLException{
        EventLog obj = new EventLog();

        obj.setBlogId(rs.getLong("blog_id"));
        obj.setCreated(rs.getTimestamp("created"));
        obj.setId(rs.getLong("id"));
        obj.setName(rs.getString("name"));
        obj.setPostId(rs.getLong("post_fk"));
        obj.setRequest(rs.getString("request"));
        obj.setUserId(rs.getLong("user_id"));

        return obj;
    }

    private PreparedStatement insertPreparedStatement(EventLog event, Connection cnn) throws SQLException{
        final String sql = "INSERT INTO event_log"
                + "(blog_id, post_fk, user_id, name, request, created)"
                + " VALUES(?,?,?,?,?,?)";

        final PreparedStatement ps = cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setLong(1, event.getBlogId());
        ps.setLong(2, event.getPostId());
        ps.setLong(3, event.getUserId());
        ps.setString(4, event.getName());
        ps.setString(5, event.getRequest());
        ps.setTimestamp(6, toTimestamp(event.getCreated()));

        return ps;
    }
}
