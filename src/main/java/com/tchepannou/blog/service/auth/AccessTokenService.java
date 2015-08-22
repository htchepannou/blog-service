package com.tchepannou.blog.service.auth;

import com.tchepannou.auth.client.v1.AccessTokenResponse;
import com.tchepannou.blog.exception.AccessTokenException;

public interface AccessTokenService {
    AccessTokenResponse get(String accessTokenId) throws AccessTokenException;
}
