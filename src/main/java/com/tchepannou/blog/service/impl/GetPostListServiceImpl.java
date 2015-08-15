package com.tchepannou.blog.service.impl;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostCollectionResponseMapper;
import com.tchepannou.blog.rr.PostCollectionResponse;
import com.tchepannou.blog.service.GetPostListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostListServiceImpl extends CommandImpl<GetPostListService.Request, PostCollectionResponse> implements GetPostListService {
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    //-- Public
    @Override
    protected PostCollectionResponse doExecute(Request request) {
        final List<Post> posts = postDao.findByBlog(request.getBlogId(), request.getLimit(), request.getOffset());

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
}
