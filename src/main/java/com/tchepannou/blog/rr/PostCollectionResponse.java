package com.tchepannou.blog.rr;

import java.util.ArrayList;
import java.util.List;

public class PostCollectionResponse {
    private List<PostResponse> posts = new ArrayList<>();

    public PostResponse getPost (int index){
        return posts.get(index);
    }

    public int getSize (){
        return posts.size();
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }
}
