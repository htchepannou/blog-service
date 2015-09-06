package com.tchepannou.blog.service.command;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.client.v1.SearchRequest;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostCollectionResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class SearchCommand extends AbstractCommand<SearchRequest, PostCollectionResponse> {
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    //-- Public
    @Override
    protected PostCollectionResponse doExecute(SearchRequest request, CommandContext context) {
        if (request.getBlogIds().isEmpty()){
            return new PostCollectionResponse();
        }

        Post.Status status = SearchRequest.DEFAULT_STATUS.equals(request.getStatus())
                ? null
                : Post.Status.valueOf(request.getStatus());

        List<Post> posts = postDao.findByBlogsByStatus(request.getBlogIds(), status, request.getLimit(), request.getOffset());

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
        return BlogConstants.METRIC_GET_POST_LIST;
    }

    @Override
    protected String getEventName() {
        return null;
    }
}
