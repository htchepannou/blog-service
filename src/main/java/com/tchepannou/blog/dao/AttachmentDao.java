package com.tchepannou.blog.dao;

import com.google.common.collect.Multimap;

import java.util.Collection;

public interface AttachmentDao {
//    List<Attachment> findByPost (long postId);
//
//    Multimap<Long, Attachment> findByPosts (Collection<Long> postIds);
//
    Multimap<Long, Long> findIdsByPosts (Collection<Long> postIds);
}
