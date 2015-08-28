package com.tchepannou.blog.service.command;

import com.tchepannou.blog.client.v1.Constants;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Attachment;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.GetPostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostCommandImpl extends AbstractCommand<Long, PostResponse> implements GetPostCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private AttachmentDao attachmentDao;

    //-- AbstractCommand overrides
    @Override
    protected String getMetricName() {
        return Constants.METRIC_GET_POST;
    }

    @Override
    public PostResponse doExecute(Long id, CommandContext context) {
        Post post = postDao.findByIdByBlog(id, context.getBlogId());
        List<Tag> tags = tagDao.findByPost(id);
        List<Attachment> attachments = attachmentDao.findByPost(id);

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .withAttachments(attachments)
                .map()
        ;
    }

    @Override
    protected String getEventName() {
        return null;
    }
}
