package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.Constants;
import com.tchepannou.blog.rr.PostListResponse;
import com.tchepannou.blog.service.GetPostListService;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostListServiceImpl extends CommandImpl<GetPostListService.Request, PostListResponse> implements GetPostListService {
    //-- Public
    @Override
    protected PostListResponse doExecute(Request request) {
        return new PostListResponse();
    }

    @Override
    protected String getMetricName() {
        return Constants.METRIC_GET_POST_LIST;
    }
}
