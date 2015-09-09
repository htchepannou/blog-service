package com.tchepannou.blog.dao.jdbc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.domain.Attachment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<Attachment> findByPost (long postId){
        return new JdbcTemplate(ds).query(
                "SELECT * FROM attachment WHERE post_fk=? AND deleted=?",
                new Object[] {postId, false},
                (rs, i) -> map(rs)
        );
    }

    @Override
    public Multimap<Long, Attachment> findByPosts(Collection<Long> postIds) {
        if (postIds.isEmpty()){
            return LinkedListMultimap.create();
        }

        List params = new ArrayList<>(postIds);
        params.add(false);
        List<Attachment> attachments = new JdbcTemplate(ds).query(
                "SELECT * FROM attachment WHERE post_fk IN (" + JdbcUtils.toParamVars(postIds) + ") AND deleted=?",
                params.toArray(),
                (rs, i) -> map(rs)
        );

        final Multimap<Long, Attachment> result = LinkedListMultimap.create();
        attachments.forEach(
                att -> result.put(att.getPostId(), att)
        );

        return result;

    }

    private Attachment map(ResultSet rs) throws SQLException {
        Attachment obj = new Attachment();
        obj.setId(rs.getLong("id"));
        obj.setPostId(rs.getLong("post_fk"));
        obj.setXvideoId(rs.getString("xid"));
        obj.setDeleted(rs.getBoolean("deleted"));
        obj.setName(rs.getString("name"));
        obj.setDescription(rs.getString("description"));
        obj.setUrl(rs.getString("url"));
        obj.setThumbnailUrl(rs.getString("thumbnail_url"));
        obj.setContentType(rs.getString("content_type"));
        obj.setContentLength(rs.getLong("content_length"));
        obj.setDurationSeconds(rs.getInt("duration_seconds"));
        obj.setWidth(rs.getInt("width"));
        obj.setHeight(rs.getInt("height"));
        obj.setCreated(rs.getDate("created"));
        obj.setOembed(rs.getBoolean("oembed"));
        return obj;
    }
}
