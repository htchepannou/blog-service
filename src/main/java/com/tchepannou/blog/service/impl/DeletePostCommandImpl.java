package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.DeletePostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DeletePostCommandImpl extends AbstractSecuredCommand<Void, Void> implements DeletePostCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private PostEntryDao postEntryDao;

    //-- AbstractSecuredCommand overrides
    @Override
    protected Void doExecute(Void request, CommandContext context) {
        final Post post = postDao.findByIdByBlog(context.getId(), context.getBlogId());

        postEntryDao.delete(post, context.getBlogId());
        if (post.getBlogId() == context.getBlogId()){
            postDao.delete(post);
        }
        return null;
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_DELETE_POST;
    }
}
