package com.tchepannou.blog.dao;

import com.google.common.collect.Multimap;

import java.util.Collection;

public interface AttachmentDao {
    Multimap<Long, Long> findIdsByPosts (Collection<Long> postIds);
}
