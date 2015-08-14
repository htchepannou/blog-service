package com.tchepannou.blog.service;

public interface Command<Request, Response> {
    Response execute (Request request);
}
