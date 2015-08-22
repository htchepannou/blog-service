package com.tchepannou.blog.service.command;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.exception.DuplicatePostException;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.ReblogPostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Collection;
import java.util.Collections;

public class ReblogPostCommandImpl extends AbstractSecuredCommand<Void, Void> implements ReblogPostCommand {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private PostEntryDao postEntryDao;


    //-- AbstractSecuredCommand overrides
    @Override
    protected Void doExecute(Void request, CommandContext context) {
        try {
            Post post = postDao.findById(context.getId());
            PostUtils.addToBlog(post, context, postEntryDao);

            return null;
        } catch (DuplicateKeyException e){
            throw new DuplicatePostException(e);
        }
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_REBLOG_POST;
    }

    @Override
    protected Collection<String> getRequiredPermissions() {
        return Collections.singletonList(Constants.PERMISSION_CREATE);
    }

    @Override protected String getEventName() {
        return Constants.EVENT_REBLOG_POST;
    }
}
