package com.tchepannou.blog.service;

import com.tchepannou.blog.rr.CreateTextRequest;
import com.tchepannou.blog.rr.PostResponse;

public interface CreateTextCommand extends Command<CreateTextRequest, PostResponse> {
}
