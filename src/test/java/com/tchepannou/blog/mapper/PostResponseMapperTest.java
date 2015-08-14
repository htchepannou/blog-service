package com.tchepannou.blog.mapper;

import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.rr.PostResponse;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class PostResponseMapperTest {
    private static long uid = System.currentTimeMillis();

    @Test
    public void testMap() throws Exception {
        // Given
        Post post = createPost();
        Tag tag1 = createTag();
        Tag tag2 = createTag();
        Tag tag3 = createTag();

        // When
        PostResponse response = new PostResponseMapper()
                .withPost(post)
                .withTags(Arrays.asList(tag1, tag2, tag3))
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
    }

    @Test(expected = IllegalStateException.class)
    public void testMap_NoPost() throws Exception {
        new PostResponseMapper()
                .map();
    }

    private Tag createTag (){
        long id = ++uid;

        Tag tag = new Tag ();
        tag.setId(id);
        tag.setName("tag_" + id);
        return tag;
    }

    private Post createPost (){
        Post post = new Post ();
        post.setBlogId(1000);
        post.setContent("<p>This is content</b>");
        post.setCreated(DateUtils.addDays(new Date(), -10));
        post.setId(++uid);
        post.setPublished(DateUtils.addDays(new Date(), -10));
        post.setSlug("This is the slug");
        post.setStatus(Post.Status.published);
        post.setTitle("Sample Post");
        post.setType(Post.Type.text);
        post.setUpdated(new Date());
        return post;
    }
}
