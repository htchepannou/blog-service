package com.tchepannou.blog.service;

public interface Command<I, O> {
    O execute (I request);
}
