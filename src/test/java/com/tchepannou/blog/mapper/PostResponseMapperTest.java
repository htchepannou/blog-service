package com.tchepannou.blog.mapper;

import com.tchepannou.blog.client.v1.AttachmentResponse;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.domain.Attachment;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import org.junit.Test;

import java.util.Arrays;

import static com.tchepannou.blog.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PostResponseMapperTest {
    @Test
    public void testMap() throws Exception {
        // Given
        Post post = createPost(100, 101);
        Tag tag1 = createTag();
        Tag tag2 = createTag();
        Tag tag3 = createTag();

        Attachment att1 = createAttachment(post.getId());
        Attachment att2 = createAttachment(post.getId());

        // When
        PostResponse response = new PostResponseMapper()
                .withPost(post)
                .withTags(Arrays.asList(tag1, tag2, tag3))
                .withAttachments(Arrays.asList(att1, att2))
                .map();

        // Then
        assertThat(response.getBlogId()).isEqualTo(post.getBlogId());
        assertThat(response.getContent()).isEqualTo(post.getContent());
        assertThat(response.getCreated()).isEqualTo(post.getCreated());
        assertThat(response.getId()).isEqualTo(post.getId());
        assertThat(response.getPublished()).isEqualTo(post.getPublished());
        assertThat(response.getSlug()).isEqualTo(post.getSlug());
        assertThat(response.getStatus()).isEqualTo(post.getStatus().name());
        assertThat(response.getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getTags()).containsExactly(tag1.getName(), tag2.getName(), tag3.getName());
        assertThat(response.getType()).isEqualTo(post.getType().name());
        assertThat(response.getUpdated()).isEqualTo(post.getUpdated());
        assertThat(response.getUserId()).isEqualTo(post.getUserId());

        assertThat(response.getAttachments()).hasSize(2);

        AttachmentResponse xatt1 = response.getAttachments().get(0);
        assertThat (xatt1.getContentLength()).isEqualTo(att1.getContentLength());
        assertThat (xatt1.getContentType()).isEqualTo(att1.getContentType());
        assertThat (xatt1.getDescription()).isEqualTo(att1.getDescription());
        assertThat (xatt1.getDurationSeconds()).isEqualTo(att1.getDurationSeconds());
        assertThat (xatt1.getHeight()).isEqualTo(att1.getHeight());
        assertThat (xatt1.getId()).isEqualTo(att1.getId());
        assertThat (xatt1.getName()).isEqualTo(att1.getName());
        assertThat (xatt1.getOembed()).isEqualTo(att1.getOembed());
        assertThat (xatt1.getPostId()).isEqualTo(att1.getPostId());
        assertThat (xatt1.getThumbnailUrl()).isEqualTo(att1.getThumbnailUrl());
        assertThat (xatt1.getUrl()).isEqualTo(att1.getUrl());
        assertThat (xatt1.getWidth()).isEqualTo(att1.getWidth());

        AttachmentResponse xatt2 = response.getAttachments().get(1);
        assertThat (xatt2.getContentLength()).isEqualTo(att2.getContentLength());
        assertThat (xatt2.getContentType()).isEqualTo(att2.getContentType());
        assertThat (xatt2.getDescription()).isEqualTo(att2.getDescription());
        assertThat (xatt2.getDurationSeconds()).isEqualTo(att2.getDurationSeconds());
        assertThat (xatt2.getHeight()).isEqualTo(att2.getHeight());
        assertThat (xatt2.getId()).isEqualTo(att2.getId());
        assertThat (xatt2.getName()).isEqualTo(att2.getName());
        assertThat (xatt2.getOembed()).isEqualTo(att2.getOembed());
        assertThat (xatt2.getPostId()).isEqualTo(att2.getPostId());
        assertThat (xatt2.getThumbnailUrl()).isEqualTo(att2.getThumbnailUrl());
        assertThat (xatt2.getUrl()).isEqualTo(att2.getUrl());
        assertThat (xatt2.getWidth()).isEqualTo(att2.getWidth());

    }

    @Test(expected = IllegalStateException.class)
    public void testMap_NoPost() throws Exception {
        new PostResponseMapper()
                .map();
    }
}
