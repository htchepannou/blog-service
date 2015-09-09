package com.tchepannou.blog.dao;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.domain.Attachment;

import java.util.Collection;
import java.util.List;

public interface AttachmentDao {
    List<Attachment> findByPost (long postId);

    Multimap<Long, Attachment> findByPosts (Collection<Long> postIds);
}
