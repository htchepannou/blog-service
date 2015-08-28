package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdatePostRequest;

public interface UpdatePostCommand extends Command<UpdatePostRequest, PostResponse>{
}
