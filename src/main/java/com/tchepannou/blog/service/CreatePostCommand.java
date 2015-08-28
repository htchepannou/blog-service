package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.CreatePostRequest;
import com.tchepannou.blog.client.v1.PostResponse;

public interface CreatePostCommand extends Command<CreatePostRequest, PostResponse> {
}
