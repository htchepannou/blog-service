package com.tchepannou.blog.service.command;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostCollectionResponseMapper;
import com.tchepannou.blog.rr.PostCollectionResponse;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.GetPostListCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostListCommandImpl extends AbstractCommand<Void, PostCollectionResponse> implements GetPostListCommand {
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    //-- Public
    @Override
    protected PostCollectionResponse doExecute(Void request, CommandContext context) {
        final List<Post> posts = postDao.findByBlog(context.getBlogId(), context.getLimit(), context.getOffset());

        final List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        final Multimap<Long, Tag> tags = tagDao.findByPosts(postIds);

        return new PostCollectionResponseMapper()
                .withPosts(posts)
                .withTags(tags)
                .map();
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_GET_POST_LIST;
    }

    @Override
    protected String getEventName() {
        return null;
    }

    @Override
    protected Collection<String> getRequiredPermissions() {
        return Collections.emptyList();
    }
}
