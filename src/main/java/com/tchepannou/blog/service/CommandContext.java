package com.tchepannou.blog.service;

public interface CommandContext {
    long getId ();
    long getBlogId();
    long getUserId();
    String getTransactionId();
    int getLimit();
    int getOffset ();
}
