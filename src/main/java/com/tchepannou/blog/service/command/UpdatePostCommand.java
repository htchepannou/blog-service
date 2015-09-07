package com.tchepannou.blog.service.command;

import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class UpdatePostCommand extends AbstractCommand<UpdatePostRequest, PostResponse> {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostTagDao postTagDao;

    //-- AbstractSecuredCommand overrides
    @Override
    protected PostResponse doExecute(UpdatePostRequest request, CommandContext context) {
        final Post post = PostUtils.updatePost(request, context, postDao);

        final List<Tag> tags = PostUtils.addTags(request, tagDao);
        PostUtils.link(post, tags, postTagDao);

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .map();
    }

    @Override
    protected String getEventName() {
        return BlogConstants.EVENT_UPDATE_POST;
    }
}
