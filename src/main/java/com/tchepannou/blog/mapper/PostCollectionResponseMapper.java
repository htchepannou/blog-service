package com.tchepannou.blog.mapper;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.domain.Attachment;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PostCollectionResponseMapper {
    //-- Attribute
    private List<Post> posts = new ArrayList<>();
    private Multimap<Long, Tag> tags = LinkedListMultimap.create();
    private Multimap<Long, Attachment> attachements = LinkedListMultimap.create();

    //-- Public
    public PostCollectionResponse map (){
        PostCollectionResponse response = new PostCollectionResponse();
        map(response, posts, tags, attachements);
        return response;
    }

    public PostCollectionResponseMapper withPosts(List<Post> posts){
        this.posts = posts;
        return this;
    }

    public PostCollectionResponseMapper withTags(Multimap<Long, Tag> tags){
        this.tags = tags;
        return this;
    }

    public PostCollectionResponseMapper withAttachments(Multimap<Long, Attachment> attachements){
        this.attachements = attachements;
        return this;
    }

    //-- Private
    private void map(PostCollectionResponse response, List<Post> posts, Multimap<Long, Tag> tagMap, Multimap<Long, Attachment> attachmentMap){
        PostResponseMapper postMapper = new PostResponseMapper();

        response.setPosts(
                posts
                        .stream()
                        .map(post -> {
                            final long postId = post.getId();

                            final Collection<Tag> tagz = tagMap.containsKey(postId)
                                    ? tagMap.get(postId)
                                    : Collections.emptyList();

                            final Collection<Attachment> attachments = attachmentMap.containsKey(postId)
                                    ? attachmentMap.get(postId)
                                    : Collections.emptyList();

                            return postMapper
                                    .withPost(post)
                                    .withTags(tagz)
                                    .withAttachments(attachments)
                                    .map();
                        })
                        .collect(Collectors.toList())
        );
    }
}
