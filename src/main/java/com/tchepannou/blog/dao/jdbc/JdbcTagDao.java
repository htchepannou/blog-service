package com.tchepannou.blog.dao.jdbc;

import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Tag;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        String sql = "SELECT T.*"
                + " FROM tag T JOIN post_tag P ON T.id=P.tag_fk"
                + " WHERE P.post_fk=?"
                + " ORDER BY P.rank;";
        return template.query(
                sql,
                new Object[]{id},
                (rs, i) -> map(rs)
        );
    }


    //-- Private
    public Tag map(ResultSet rs) throws SQLException {
        Tag tag = new Tag ();

        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));

        return tag;
    }
}
