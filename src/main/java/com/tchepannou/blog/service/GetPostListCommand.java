package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.PostCollectionResponse;

public interface GetPostListCommand extends Command<Void, PostCollectionResponse> {
}
