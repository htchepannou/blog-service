package com.tchepannou.blog.controller;

import com.tchepannou.blog.service.CommandContext;

public class CommandContextImpl implements CommandContext {
    //-- Attributes
    private long blogId;
    private String accessTokenId;
    private int limit;
    private int offset;

    //-- Public
    public CommandContextImpl withBlogId(long blogId){
        this.blogId = blogId;
        return this;
    }

    public CommandContextImpl withAccessTokenId(String accessTokenId){
        this.accessTokenId = accessTokenId;
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

    //-- CommandContext overrides
    @Override
    public long getBlogId() {
        return blogId;
    }

    @Override
    public String getAccessTokenId() {
        return accessTokenId;
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
