package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.domain.AccessToken;
import com.tchepannou.blog.service.AccessTokenService;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.security.core.AuthenticationException;

import java.util.OptionalLong;

public abstract class AbstractSecuredCommand<I, O> extends AbstractCommand<I, O> {
    //-- Attributes
    //@Autowired
    private AccessTokenService accessTokenService;

    private AccessToken accessToken;


    //-- Protected
    @Override
    protected void authenticate (CommandContext context) throws AuthenticationException {
        accessToken = accessTokenService.get(context.getAccessTokenId());
    }

    //-- Getter
    public OptionalLong getUserId (){
        return OptionalLong.of(accessToken.getUserId());
    }
}
