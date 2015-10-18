package com.tchepannou.blog.mapper;

import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import org.junit.Test;

import java.util.Arrays;

import static com.tchepannou.blog.Fixture.createPost;
import static com.tchepannou.blog.Fixture.createTag;
import static org.assertj.core.api.Assertions.assertThat;

public class PostResponseMapperTest {
    @Test
    public void testMap() throws Exception {
        // Given
        Post post = createPost(100, 101);
        Tag tag1 = createTag();
        Tag tag2 = createTag();
        Tag tag3 = createTag();

        // When
        PostResponse response = new PostResponseMapper()
                .withPost(post)
                .withTags(Arrays.asList(tag1, tag2, tag3))
                .withAttachmentIds(Arrays.asList(1L, 2L, 3L))
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
        assertThat(response.getUpdated()).isEqualTo(post.getUpdated());
        assertThat(response.getUserId()).isEqualTo(post.getUserId());

        assertThat(response.getAttachmentIds()).hasSize(3);
        assertThat(response.getAttachmentIds()).contains(1L, 2L, 3L);
    }

    @Test(expected = IllegalStateException.class)
    public void testMap_NoPost() throws Exception {
        new PostResponseMapper()
                .map();
    }
}
