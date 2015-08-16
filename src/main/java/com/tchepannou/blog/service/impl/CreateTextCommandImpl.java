package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.rr.CreateTextRequest;
import com.tchepannou.blog.rr.PostRequest;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.CreateTextCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class CreateTextCommandImpl extends AbstractSecuredCommand<CreateTextRequest, PostResponse> implements CreateTextCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    //-- AbstractCommand overrides
    @Override
    protected PostResponse doExecute(CreateTextRequest request, CommandContext context) {
        final Post post = createPost(request, context, Post.Type.text, postDao);

        return new PostResponseMapper()
                .withPost(post)
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
}
