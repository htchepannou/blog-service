package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.exception.AuthorizationException;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.auth.AccessToken;
import com.tchepannou.blog.service.auth.AccessTokenService;
import com.tchepannou.blog.service.auth.PermissionCollection;
import com.tchepannou.blog.service.auth.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.OptionalLong;

public abstract class AbstractSecuredCommand<I, O> extends AbstractCommand<I, O> {
    //-- Attributes
    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private PermissionService permissionService;

    private AccessToken accessToken;


    //-- Protected
    @Override
    protected void authenticate (CommandContext context){
        String id = context.getAccessTokenId();
        getLogger().info(String.format("Authenticating {}", id));
        accessToken = accessTokenService.get(id);
    }

    @Override
    protected void authorize(CommandContext context) {
        List<String> permissions = getPermissions(context);
        if (permissions.isEmpty()){
            return;
        }

        PermissionCollection pc = permissionService.get(context.getBlogId(), getUserId().getAsLong());
        if (!pc.getPermissions().containsAll(permissions)){
            throw new AuthorizationException(permissions.toString());
        }
    }

    //-- Getter
    @Override
    public OptionalLong getUserId (){
        return OptionalLong.of(accessToken.getUserId());
    }
}
