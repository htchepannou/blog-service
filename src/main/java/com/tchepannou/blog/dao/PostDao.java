package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.Post;

public interface PostDao {
    Post findById (long id);
}
