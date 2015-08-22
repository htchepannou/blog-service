package com.tchepannou.blog.service.auth;

import com.tchepannou.auth.client.v1.PermissionCollectionResponse;

public interface PermissionService {
    PermissionCollectionResponse get (long blogId, long userId);
}
