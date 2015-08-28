package com.tchepannou.blog.service.command;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.client.v1.CreatePostRequest;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.CreatePostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
@Transactional
public class CreatePostCommandImpl extends AbstractCommand<CreatePostRequest, PostResponse> implements CreatePostCommand {
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
    protected PostResponse doExecute(CreatePostRequest request, CommandContext context) {
        final Post post = PostUtils.createPost(request, context, request.getUserId(), postDao);
        final List<Tag> tags = PostUtils.addTags(request, tagDao);
        PostUtils.link(post, tags, postTagDao);
        PostUtils.addToBlog(post, context, postEntryDao);

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .map();
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_CREATE_TEXT;
    }

    @Override
    protected String getEventName() {
        return Constants.EVENT_CREATE_TEXT;
    }
}
