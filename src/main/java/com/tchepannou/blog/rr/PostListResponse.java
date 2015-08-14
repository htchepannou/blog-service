package com.tchepannou.blog.rr;

import java.util.ArrayList;
import java.util.List;

public class PostListResponse {
    private List<PostResponse> posts = new ArrayList<>();

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }
}
