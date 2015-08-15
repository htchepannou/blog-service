package com.tchepannou.blog.mapper;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.rr.PostCollectionResponse;
import org.junit.Test;

import java.util.Arrays;

import static com.tchepannou.blog.Fixture.createPost;
import static com.tchepannou.blog.Fixture.createTag;
import static org.assertj.core.api.Assertions.assertThat;

public class PostCollectionResponseMapperTest {

    @Test
    public void testMap() throws Exception {
        // Given
        Post post1 = createPost(100, 101);
        Post post2 = createPost(100, 102);

        Tag tag1 = createTag();
        Tag tag2 = createTag();
        Tag tag3 = createTag();

        Multimap tags = LinkedListMultimap.create();
        tags.put(post1.getId(), tag1);
        tags.put(post1.getId(), tag3);

        tags.put(post2.getId(), tag1);
        tags.put(post2.getId(), tag2);

        // When
        PostCollectionResponse response = new PostCollectionResponseMapper()
                .withPosts(Arrays.asList(post1, post2))
                .withTags(tags)
                .map();

        // Then
        assertThat(response.getSize()).isEqualTo(2);
        
        assertThat(response.getPost(0).getBlogId()).isEqualTo(post1.getBlogId());
        assertThat(response.getPost(0).getContent()).isEqualTo(post1.getContent());
        assertThat(response.getPost(0).getCreated()).isEqualTo(post1.getCreated());
        assertThat(response.getPost(0).getId()).isEqualTo(post1.getId());
        assertThat(response.getPost(0).getPublished()).isEqualTo(post1.getPublished());
        assertThat(response.getPost(0).getSlug()).isEqualTo(post1.getSlug());
        assertThat(response.getPost(0).getStatus()).isEqualTo(post1.getStatus().name());
        assertThat(response.getPost(0).getTitle()).isEqualTo(post1.getTitle());
        assertThat(response.getPost(0).getType()).isEqualTo(post1.getType().name());
        assertThat(response.getPost(0).getUpdated()).isEqualTo(post1.getUpdated());
        assertThat(response.getPost(0).getTags()).containsExactly(tag1.getName(), tag3.getName());
        assertThat(response.getPost(0).getUserId()).isEqualTo(post1.getUserId());

        assertThat(response.getPost(1).getBlogId()).isEqualTo(post2.getBlogId());
        assertThat(response.getPost(1).getContent()).isEqualTo(post2.getContent());
        assertThat(response.getPost(1).getCreated()).isEqualTo(post2.getCreated());
        assertThat(response.getPost(1).getId()).isEqualTo(post2.getId());
        assertThat(response.getPost(1).getPublished()).isEqualTo(post2.getPublished());
        assertThat(response.getPost(1).getSlug()).isEqualTo(post2.getSlug());
        assertThat(response.getPost(1).getStatus()).isEqualTo(post2.getStatus().name());
        assertThat(response.getPost(1).getTitle()).isEqualTo(post2.getTitle());
        assertThat(response.getPost(1).getType()).isEqualTo(post2.getType().name());
        assertThat(response.getPost(1).getUpdated()).isEqualTo(post2.getUpdated());
        assertThat(response.getPost(1).getTags()).containsExactly(tag1.getName(), tag2.getName());
        assertThat(response.getPost(1).getUserId()).isEqualTo(post2.getUserId());
    }
}
