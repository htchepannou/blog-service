package com.tchepannou.blog.rr;

import java.util.List;

public class PostListResponse {
    private List<PostResponse> posts;

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }
}
