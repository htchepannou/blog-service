package com.tchepannou.blog.service.auth;

public interface PermissionService {
    PermissionCollection get (long blogId, long userId);
}
