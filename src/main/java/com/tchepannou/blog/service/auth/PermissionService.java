package com.tchepannou.blog.service.auth;

public interface PermissionService {
    PermissionCollection find (long blogId, long userId);
}
