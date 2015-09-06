package com.tchepannou.blog.dao;

import com.tchepannou.blog.domain.Post;

import java.util.Collection;
import java.util.List;

public interface PostDao {
    Post findById (long id);

    Post findByIdByBlog (long id, long blogId);

    List<Post> findByBlog(long blogId, int limit, int offset);

    List<Post> findByBlogsByStatus(Collection<Long> blogIds, Post.Status status, int limit, int offset);

    void create (Post post);

    void update(Post post);

    void delete(Post post);
}
