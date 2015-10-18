package com.tchepannou.blog.dao.jdbc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tchepannou.blog.dao.AttachmentDao;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JdbcAttachmentDao implements AttachmentDao {
    private DataSource ds;

    public JdbcAttachmentDao(DataSource ds){
        this.ds = ds;
    }

    //-- Attachment overrides
    @Override
    public Multimap<Long, Long> findIdsByPosts(Collection<Long> postIds) {
        if (postIds.isEmpty()){
            return LinkedListMultimap.create();
        }

        final String sql = "SELECT * FROM post_attachment"
                + " JOIN attachment ON id=attachment_fk"
                + " WHERE post_fk IN (" + JdbcUtils.toParamVars(postIds) + ")"
                + " AND deleted=?"
                + " ORDER BY rank";

        final Multimap<Long, Long> result = LinkedListMultimap.create();
        List params = new ArrayList<>(postIds);
        params.add(false);
        new JdbcTemplate(ds).query(
                sql,
                params.toArray(),
                (rs, i) -> {
                    result.put(rs.getLong("post_fk"), rs.getLong("attachment_fk"));
                    return result;
                }
        );

        return result;

    }
}
