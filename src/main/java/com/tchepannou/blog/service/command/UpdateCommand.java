package com.tchepannou.blog.service.command;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Transactional
public class UpdateCommand extends AbstractCommand<UpdatePostRequest, PostResponse> {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostTagDao postTagDao;

    @Autowired
    private AttachmentDao attachmentDao;

    //-- AbstractSecuredCommand overrides
    @Override
    protected PostResponse doExecute(UpdatePostRequest request, CommandContext context) {
        final long id = context.getId();
        final Post post = PostUtils.updatePost(request, context, postDao);

        final List<Tag> tags = PostUtils.addTags(request, tagDao);
        PostUtils.link(post, tags, postTagDao);

        final Multimap<Long, Long> attachmentIds = attachmentDao.findIdsByPosts(Collections.singleton(id));

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .withAttachmentIds(attachmentIds.get(id))
                .map();
    }

    @Override
    protected String getEventName() {
        return BlogConstants.EVENT_UPDATE_POST;
    }
}
