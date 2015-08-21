package com.tchepannou.blog.service.command;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.rr.CreateTextRequest;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.CreateTextCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        final Post post = PostUtils.createPost(request, context, getUserId().getAsLong(), Post.Type.text, postDao);
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

    @Override
    protected Collection<String> getRequiredPermissions() {
        return Collections.singletonList(Constants.PERMISSION_CREATE);
    }
}
