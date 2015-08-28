package com.tchepannou.blog.controller;

import com.tchepannou.blog.service.CommandContext;

public class CommandContextImpl implements CommandContext {
    //-- Attributes
    private long id;
    private long blogId;
    private long userId;
    private int limit;
    private int offset;

    //-- Public
    public CommandContextImpl withBlogId(long blogId){
        this.blogId = blogId;
        return this;
    }

    public CommandContextImpl withUserId(long userId){
        this.userId = userId;
        return this;
    }

    public CommandContextImpl withLimit (int limit){
        this.limit = limit;
        return this;
    }

    public CommandContextImpl withOffset (int offset){
        this.offset = offset;
        return this;
    }

    public CommandContextImpl withId (long id){
        this.id = id;
        return this;
    }

    //-- CommandContext overrides
    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getBlogId() {
        return blogId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public int getOffset() {
        return offset;
    }
}
