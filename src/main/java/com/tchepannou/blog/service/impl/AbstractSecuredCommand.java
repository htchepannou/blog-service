package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.domain.AccessToken;
import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.blog.service.AccessTokenService;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalLong;

public abstract class AbstractSecuredCommand<I, O> extends AbstractCommand<I, O> {
    //-- Attributes
    @Autowired
    private AccessTokenService accessTokenService;

    private AccessToken accessToken;


    //-- Protected
    @Override
    protected void authenticate (CommandContext context) throws AccessTokenException {
        accessToken = accessTokenService.get(context.getAccessTokenId());
    }

    //-- Getter
    @Override
    public OptionalLong getUserId (){
        return OptionalLong.of(accessToken.getUserId());
    }
}
