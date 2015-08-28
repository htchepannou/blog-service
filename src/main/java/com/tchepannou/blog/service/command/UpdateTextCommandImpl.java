package com.tchepannou.blog.service.command;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.UpdatePostCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Transactional
public class UpdateTextCommandImpl extends AbstractSecuredCommand<UpdatePostRequest, PostResponse> implements UpdatePostCommand {
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
    protected String getMetricName() {
        return Constants.METRIC_UPDATE_TEXT;
    }

    @Override
    protected String getEventName() {
        return Constants.EVENT_UPDATE_TEXT;
    }

    @Override
    protected Collection<String> getRequiredPermissions() {
        return Collections.singletonList(Constants.PERMISSION_EDIT);
    }

    @Override
    protected Collection<String> getPermissions(CommandContext context) {
        Collection<String> permissions = super.getPermissions(context);
        if (!isAnonymousUser() && !permissions.contains(Constants.PERMISSION_EDIT)){
            Post post = getPost(context);
            if (post.getUserId() == getUserId().getAsLong()){
                permissions.add(Constants.PERMISSION_EDIT);
            }
        }
        return permissions;
    }


    private Post getPost (CommandContext context){
        return postDao.findByIdByBlog(context.getId(), context.getBlogId());
    }

}
