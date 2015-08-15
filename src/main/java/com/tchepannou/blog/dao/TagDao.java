package com.tchepannou.blog.dao;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.domain.Tag;

import java.util.Collection;
import java.util.List;

public interface TagDao {
    List<Tag> findByIds(Collection<Long> ids);

    List<Tag> findByPost(long postId);

    Multimap<Long, Tag> findByPosts(Collection<Long> posts);
}
