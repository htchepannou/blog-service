package com.tchepannou.blog.service.command;

import com.tchepannou.blog.client.v1.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.ReblogPostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

public class ReblogPostCommandImpl extends AbstractCommand<Void, Boolean> implements ReblogPostCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private PostEntryDao postEntryDao;


    //-- AbstractSecuredCommand overrides
    @Override
    protected Boolean doExecute(Void request, CommandContext context) {
        try {
            Post post = postDao.findById(context.getId());
            PostUtils.addToBlog(post, context, postEntryDao);

            return true;
        } catch (DuplicateKeyException e){  // NOSONAR
            return false;
        }
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_REBLOG_POST;
    }

    @Override protected String getEventName() {
        return Constants.EVENT_REBLOG_POST;
    }
}
