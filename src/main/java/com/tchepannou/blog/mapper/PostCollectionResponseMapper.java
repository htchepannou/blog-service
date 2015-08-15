package com.tchepannou.blog.mapper;

import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.rr.PostCollectionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostCollectionResponseMapper {
    //-- Attribute
    private List<Post> posts = new ArrayList<>();

    //-- Public
    public PostCollectionResponse map (){
        PostCollectionResponse response = new PostCollectionResponse();
        map(response, posts);
        return response;
    }

    public PostCollectionResponseMapper withPosts(List<Post> posts){
        this.posts = posts;
        return this;
    }

    //-- Private
    private void map(PostCollectionResponse response, List<Post> posts){
        PostResponseMapper postMapper = new PostResponseMapper();

        response.setPosts(
            posts
                    .stream()
                    .map(post -> postMapper.withPost(post).map())
                    .collect(Collectors.toList())
        );
    }
}
