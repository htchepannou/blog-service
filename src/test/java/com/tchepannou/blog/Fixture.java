package com.tchepannou.blog;

import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;

import java.util.Date;

public class Fixture {
    private static long uid = System.currentTimeMillis();

    public static Tag createTag (){
        long id = ++uid;

        Tag tag = new Tag ();
        tag.setId(id);
        tag.setName("tag_" + id);
        return tag;
    }

    public static Post createPost (long blogId, long userId){
        long id = ++uid;

        Post post = new Post ();
        post.setId(id);
        post.setBlogId(blogId);
        post.setUserId(userId);
        post.setContent("<p>This is content" + id + "</b>");
        post.setCreated(new Date ());
        post.setPublished(new Date ());
        post.setSlug("This is the slug" + id);
        post.setStatus(Post.Status.published);
        post.setTitle("Sample Post " + id);
        post.setType(Post.Type.text);
        post.setUpdated(new Date());
        return post;
    }

}
