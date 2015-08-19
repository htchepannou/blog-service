package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.rr.UpdateTextRequest;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.UpdateTextCommand;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UpdateTextCommandImpl extends AbstractSecuredCommand<UpdateTextRequest, PostResponse> implements UpdateTextCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostTagDao postTagDao;

    @Autowired
    private PostEntryDao postEntryDao;

    //-- AbstractSecuredCommand overrides
    @Override
    protected PostResponse doExecute(UpdateTextRequest request, CommandContext context) {
        final Post post = PostUtils.updatePost(request, context, postDao);

        final List<Tag> tags = PostUtils.addTags(request, tagDao);
        PostUtils.link(post, tags, postTagDao);

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .map();

    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_UPDATE_TEXT;
    }
}
