package com.tchepannou.blog.service;

import com.tchepannou.blog.domain.AccessToken;
import com.tchepannou.blog.exception.AccessTokenException;

public interface AccessTokenService {
    AccessToken get(String accessTokenId) throws AccessTokenException;
}
