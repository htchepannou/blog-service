package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostEntry;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.exception.AuthorizationException;
import com.tchepannou.blog.rr.PostRequest;
import com.tchepannou.blog.rr.UpdateTextRequest;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.core.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostUtils {
    public static Post createPost(PostRequest request, CommandContext context, long userId, Post.Type type, PostDao dao){
        Post post = new Post();
        post.setBlogId(context.getBlogId());
        post.setContent(request.getContent());
        post.setPublished(request.getPublished());
        post.setSlug(request.getSlug());
        post.setStatus(Enum.valueOf(Post.Status.class, request.getStatus()));
        post.setTitle(request.getTitle());
        post.setType(type);
        post.setUserId(userId);
        dao.create(post);

        return post;
    }

    public static Post updatePost(UpdateTextRequest request, CommandContext context, PostDao dao) {
        Post post = dao.findByIdByBlog(context.getId (), context.getBlogId());
        if (post == null){
            ArrayList<Long> ids = new ArrayList<>();
            ids.add(context.getId());
            ids.add(context.getBlogId());
            throw new NotFoundException(ids, Post.class);
        }
        if (post.getBlogId() != context.getBlogId()){
            throw new AuthorizationException("invalid_blog");
        }

        post.setBlogId(context.getBlogId());
        post.setContent(request.getContent());
        post.setPublished(request.getPublished());
        post.setSlug(request.getSlug());
        post.setStatus(Enum.valueOf(Post.Status.class, request.getStatus()));
        post.setTitle(request.getTitle());
        dao.update(post);
        return post;
    }

    public static List<Tag> addTags(PostRequest request, TagDao dao){
        /* get existing tags */
        final List<Tag> existing = dao.findByNames(request.getTags());
        final Set<String> existingNames = existing
                .stream()
                .map(i -> i.getName().toLowerCase())
                .collect(Collectors.toSet());

        /* add new tags */
        List<String> addNames = request.getTags().stream()
                .filter(tag -> !existingNames.contains(tag.toLowerCase()))
                .collect(Collectors.toList());

        /* add the tags */
        List<Tag> add = addNames.stream()
                .map(i -> create(i, dao))
                .collect(Collectors.toList());

        List<Tag> result = new ArrayList<>();
        result.addAll(existing);
        result.addAll(add);
        return result;
    }

    public static void link(Post post, List<Tag> tags, PostTagDao dao){
        dao.deleteByPost(post.getId());

        List<Long> tagIds = tags
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        dao.add(post.getId(), tagIds);
    }

    public static void addToBlog(Post post, CommandContext context, PostEntryDao dao){
        final PostEntry entry = new PostEntry(post.getId(), context.getBlogId());
        dao.create(entry);
    }

    public static Tag create(String name, TagDao dao){
        Tag tag = new Tag ();
        tag.setName(name);
        dao.create(tag);

        return tag;
    }

}
