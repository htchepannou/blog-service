package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.mapper.PostCollectionResponseMapper;
import com.tchepannou.blog.rr.PostCollectionResponse;
import com.tchepannou.blog.service.GetPostListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostListServiceImpl extends CommandImpl<GetPostListService.Request, PostCollectionResponse> implements GetPostListService {
    @Autowired
    private PostDao postDao;

    //-- Public
    @Override
    protected PostCollectionResponse doExecute(Request request) {
        List<Post> posts = postDao.findByBlog(request.getBlogId(), request.getLimit(), request.getOffset());
        return new PostCollectionResponseMapper()
                .withPosts(posts)
                .map();
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_GET_POST_LIST;
    }
}
