package com.tchepannou.blog.service.auth;

import java.util.HashSet;
import java.util.Set;

public class PermissionCollection {
    //-- Attributes
    private long userId;
    private long spaceId;
    private String application;
    private Set<String> permissions = new HashSet<>();

    //-- Getter/Setter
    public Set<String> getPermissions() {
        return permissions;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }}
