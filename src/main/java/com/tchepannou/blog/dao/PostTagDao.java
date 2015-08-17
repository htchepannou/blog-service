package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.PostTag;

import java.util.Collection;
import java.util.List;

public interface PostTagDao {
    List<PostTag> findByPost(long postId);

    List<PostTag> findByPosts(Collection<Long> postIds);

    void deleteByPost(long postId);

    void add(long postId, List<Long> tagIds);
}
