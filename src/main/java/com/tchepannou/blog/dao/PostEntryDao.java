package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.PostEntry;

import java.util.List;

public interface PostEntryDao {
    List<PostEntry> findByPost(long postId);

    void create (PostEntry entry);
}
