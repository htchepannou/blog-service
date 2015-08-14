package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.Tag;

import java.util.List;

public interface TagDao {
    List<Tag> findByPost(long postId);
}
