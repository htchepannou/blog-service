package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdateTextRequest;

public interface UpdateTextCommand extends Command<UpdateTextRequest, PostResponse>{
}
