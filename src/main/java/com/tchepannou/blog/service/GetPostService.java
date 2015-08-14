package com.tchepannou.blog.service;

import com.tchepannou.blog.rr.PostResponse;

public interface GetPostService extends Command<Long, PostResponse> {
    PostResponse execute (Long id);
}
