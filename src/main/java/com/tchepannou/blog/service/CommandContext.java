package com.tchepannou.blog.service;

public interface CommandContext {
    long getId ();
    long getBlogId();
    String getAccessTokenId();
    int getLimit();
    int getOffset ();
}
