package com.tchepannou.blog.service.auth;

import com.tchepannou.blog.domain.Model;

public class AccessToken extends Model {
    private String id;
    private long userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
