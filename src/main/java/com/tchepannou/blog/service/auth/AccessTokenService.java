package com.tchepannou.blog.service.auth;

import com.tchepannou.blog.exception.AccessTokenException;

public interface AccessTokenService {
    AccessToken get(String accessTokenId) throws AccessTokenException;
}
