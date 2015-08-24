package com.tchepannou.blog.service.auth;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.auth.client.v1.AccessTokenResponse;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.core.http.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

public class AccessTokenServiceImpl implements AccessTokenService{
    @Autowired
    private MetricRegistry metrics;

    @Value("${auth.hostname}")
    private String hostname;

    @Value("${auth.port}")
    private int port;

    @Value("${auth.access_token.path}")
    private String path;

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    //-- AccessTokenService overrides
    @Override
    public AccessTokenResponse get(String accessTokenId) throws AccessTokenException {
        Timer.Context timer = metrics.timer(Constants.METRIC_AUTH_DURATION).time();
        try {
            metrics.meter(Constants.METRIC_AUTH_COUNT).mark();

            if (accessTokenId != null) {
                return http()
                        .withPath(String.format("%s/%s", path, accessTokenId))
                        .get(AccessTokenResponse.class);
            } else {
                metrics.meter(Constants.METRIC_AUTH_ERRORS).mark();
                throw new AccessTokenException("auth_failed");
            }

        } catch (IOException e){
            metrics.meter(Constants.METRIC_AUTH_ERRORS).mark();

            throw new AccessTokenException("auth_failed", e);
        } finally {
            timer.stop();
        }
    }

    private Http http (){
        return new Http()
                .withHost(hostname)
                .withPort(port)
                .withObjectMapper(jackson.build())
        ;
    }
}
