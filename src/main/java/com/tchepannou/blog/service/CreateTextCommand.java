package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.CreateTextRequest;
import com.tchepannou.blog.client.v1.PostResponse;

public interface CreateTextCommand extends Command<CreateTextRequest, PostResponse> {
}
