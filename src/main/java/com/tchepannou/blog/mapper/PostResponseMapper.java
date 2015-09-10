package com.tchepannou.blog.mapper;

import com.google.common.base.Preconditions;
import com.tchepannou.blog.client.v1.AttachmentResponse;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.domain.Attachment;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class PostResponseMapper {
    //-- Attribute
    private static final int WEIGHT_VIDEO = 8;
    private static final int WEIGHT_OEMBED = 4;
    private static final int WEIGHT_IMAGE = 2;

    private Post post;
    private Collection<Tag> tags = new ArrayList<>();
    private Collection<Attachment> attachments = new ArrayList<>();

    //-- Public
    public PostResponse map (){
        Preconditions.checkState(post != null, "post == null");

        PostResponse response = new PostResponse();
        map(response, post);
        mapTags(response, tags);
        mapAttachments(response, attachments);
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

    public PostResponseMapper withAttachments(Collection<Attachment> attachments){
        this.attachments = attachments;
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

    private void mapAttachments(PostResponse response, Collection<Attachment> attachments){
        final AttachmentResponseMapper mapper = new AttachmentResponseMapper();

        attachments.stream()
                .forEach(attachment -> response.addAttachment(mapper.withAttachment(attachment).build()) );

        if (!response.getAttachments().isEmpty()){
            Optional<AttachmentResponse> mainAttachment = response.getAttachments().stream()
                    .filter(i -> weight(i) > 0)
                    .sorted((i, j) -> weight(j) - weight(i))
                    .findFirst();

            if (mainAttachment.isPresent()) {
                response.setMainAttachmentId(mainAttachment.get().getId());
            }
        }
    }

    private int weight(AttachmentResponse attachment){
        if (attachment.isVideo()){
            return WEIGHT_VIDEO;
        } else if (attachment.isImage()){
            return WEIGHT_IMAGE;
        } else if (attachment.getOembed() == Boolean.TRUE){
            return WEIGHT_OEMBED;
        }
        return 0;
    }
}
