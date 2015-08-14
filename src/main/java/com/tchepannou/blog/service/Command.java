package com.tchepannou.blog.service;

public interface Command<RequestType, ResponseType> {
    ResponseType execute (RequestType request);
}
