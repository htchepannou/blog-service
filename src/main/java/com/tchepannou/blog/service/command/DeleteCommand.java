package com.tchepannou.blog.service.command;

import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DeleteCommand extends AbstractCommand<Void, Void> {
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
    protected String getEventName() {
        return BlogConstants.EVENT_DELETE_POST;
    }

    private Post getPost (CommandContext context){
        return postDao.findByIdByBlog(context.getId(), context.getBlogId());
    }
}
