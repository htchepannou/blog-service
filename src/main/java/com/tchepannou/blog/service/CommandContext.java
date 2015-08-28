package com.tchepannou.blog.service;

public interface CommandContext {
    long getId ();
    long getBlogId();
    long getUserId();
    int getLimit();
    int getOffset ();
}
