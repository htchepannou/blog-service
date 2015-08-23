package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.PostResponse;

public interface GetPostCommand extends Command<Long, PostResponse> {
}
