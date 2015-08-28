package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.Attachment;

import java.util.List;

public interface AttachmentDao {
    List<Attachment> findByPost (long postId);
}
