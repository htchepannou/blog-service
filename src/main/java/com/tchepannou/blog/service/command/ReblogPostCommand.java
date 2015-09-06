package com.tchepannou.blog.service.command;

import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

public class ReblogPostCommand extends AbstractCommand<Void, Boolean> {
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
        return BlogConstants.METRIC_REBLOG_POST;
    }

    @Override protected String getEventName() {
        return BlogConstants.EVENT_REBLOG_POST;
    }
}
