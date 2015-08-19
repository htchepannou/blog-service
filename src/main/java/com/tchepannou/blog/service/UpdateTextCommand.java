package com.tchepannou.blog.service;

import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.rr.UpdateTextRequest;

public interface UpdateTextCommand extends Command<UpdateTextRequest, PostResponse>{
}
