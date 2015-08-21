package com.tchepannou.blog.service.command;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.DeletePostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

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
        final Post post = getPost(context);

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

    @Override
    protected String getEventName() {
        return Constants.EVENT_DELETE_POST;
    }

    @Override
    protected Collection<String> getRequiredPermissions() {
        return Collections.singletonList(Constants.PERMISSION_DELETE);
    }

    @Override
    protected Collection<String> getPermissions(CommandContext context) {
        Collection<String> permissions = super.getPermissions(context);
        if (!permissions.contains(Constants.PERMISSION_DELETE) && !isAnonymousUser()){
            Post post = getPost(context);
            if (post.getUserId() == getUserId().getAsLong()){
                permissions.add(Constants.PERMISSION_DELETE);
            }
        }
        return permissions;
    }

    private Post getPost (CommandContext context){
        return postDao.findByIdByBlog(context.getId(), context.getBlogId());
    }
}
