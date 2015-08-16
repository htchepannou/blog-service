package com.tchepannou.blog.domain;

import java.util.Date;

public class AccessToken {
    private String id;
    private long userId;
    private Date expiryDate;

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
