package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostEntry;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.rr.CreateTextRequest;
import com.tchepannou.blog.rr.PostRequest;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.CreateTextCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
@Transactional
public class CreateTextCommandImpl extends AbstractSecuredCommand<CreateTextRequest, PostResponse> implements CreateTextCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostTagDao postTagDao;

    @Autowired
    private PostEntryDao postEntryDao;

    //-- AbstractCommand overrides
    @Override
    protected PostResponse doExecute(CreateTextRequest request, CommandContext context) {
        final Post post = createPost(request, context, Post.Type.text, postDao);
        final List<Tag> tags = addTags(request, tagDao);
        link(post, tags, postTagDao);
        addToBlog(post, context);

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .map();
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_CREATE_TEXT;
    }


    //-- Private
    private Post createPost(PostRequest request, CommandContext context, Post.Type type, PostDao dao){
        Post post = new Post();
        post.setBlogId(context.getBlogId());
        post.setContent(request.getContent());
        post.setPublished(request.getPublished());
        post.setSlug(request.getSlug());
        post.setStatus(Enum.valueOf(Post.Status.class, request.getStatus()));
        post.setTitle(request.getTitle());
        post.setType(type);
        post.setUserId(getUserId().getAsLong());
        dao.create(post);

        return post;
    }

    private List<Tag> addTags(PostRequest request, TagDao dao){
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

    private void link(Post post, List<Tag> tags, PostTagDao dao){
        dao.deleteByPost(post.getId());

        List<Long> tagIds = tags
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        dao.add(post.getId(), tagIds);
    }

    private void addToBlog(Post post, CommandContext context){
        final PostEntry entry = new PostEntry(post.getId(), context.getBlogId());
        postEntryDao.create(entry);
    }

    private Tag create(String name, TagDao dao){
        Tag tag = new Tag ();
        tag.setName(name);
        dao.create(tag);

        return tag;
    }
}
