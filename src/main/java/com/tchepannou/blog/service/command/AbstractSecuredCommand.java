package com.tchepannou.blog.service.command;

import com.tchepannou.auth.client.v1.AccessTokenResponse;
import com.tchepannou.blog.exception.AuthorizationException;
import com.tchepannou.blog.service.CommandContext;
import com.tchepannou.blog.service.auth.AccessTokenService;
import com.tchepannou.blog.service.auth.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.OptionalLong;

public abstract class AbstractSecuredCommand<I, O> extends AbstractCommand<I, O> {
    //-- Attributes
    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private PermissionService permissionService;

    private AccessTokenResponse accessToken;


    //-- AbstractCommand overrides
    @Override
    protected void authenticate (CommandContext context){
        String id = context.getAccessTokenId();
        getLogger().info("Authenticating {}", id);
        accessToken = accessTokenService.get(id);
    }

    @Override
    protected void authorize(CommandContext context) {
        Collection<String> permissions = getRequiredPermissions();
        if (permissions.isEmpty()){
            return;
        }

        if (!getPermissions(context).containsAll(permissions)){
            throw new AuthorizationException("bad_permission");
        }
    }

    @Override
    public OptionalLong getUserId (){
        return OptionalLong.of(accessToken.getUserId());
    }

    //-- Protected
    protected Collection<String> getPermissions (CommandContext context){
        return permissionService.get(context.getBlogId(), getUserId().getAsLong()).getPermissions();
    }
}
