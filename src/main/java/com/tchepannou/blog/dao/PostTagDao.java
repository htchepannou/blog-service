package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.PostTag;

import java.util.Collection;
import java.util.List;

public interface PostTagDao {
    List<PostTag> findByPosts(Collection<Long> postIds);
}
