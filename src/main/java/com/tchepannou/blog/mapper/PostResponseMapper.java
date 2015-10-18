package com.tchepannou.blog.mapper;

import com.google.common.base.Preconditions;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;

import java.util.ArrayList;
import java.util.Collection;

public class PostResponseMapper {
    //-- Attribute
    private Post post;
    private Collection<Tag> tags = new ArrayList<>();
    private Collection<Long> attachmentIds = new ArrayList<>();

    //-- Public
    public PostResponse map (){
        Preconditions.checkState(post != null, "post == null");

        PostResponse response = new PostResponse();
        map(response, post);
        mapTags(response, tags);
        mapAttachments(response, attachmentIds);
        return response;
    }

    public PostResponseMapper withPost (Post post){
        this.post = post;
        return this;
    }
    public PostResponseMapper withTags (Collection<Tag> tags){
        this.tags = tags;
        return this;
    }

    public PostResponseMapper withAttachmentIds(Collection<Long> attachmentIds){
        this.attachmentIds = attachmentIds;
        return this;
    }

    //-- Private
    private void map(PostResponse response, Post post){
        response.setBlogId(post.getBlogId());
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setSlug(post.getSlug());
        response.setCreated(post.getCreated());
        response.setPublished(post.getPublished());
        response.setStatus(post.getStatus().name());
        response.setTitle(post.getTitle());
        response.setUpdated(post.getUpdated());
        response.setUserId(post.getUserId());
    }

    private void mapTags(PostResponse response, Collection<Tag> tags){
        tags.stream()
            .forEach(tag -> response.addTag(tag.getName()));
    }

    private void mapAttachments(PostResponse response, Collection<Long> attachmentIds){
        attachmentIds.stream()
                .forEach(id -> response.addAttachment(id) );
    }
}
