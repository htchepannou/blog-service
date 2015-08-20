package com.tchepannou.blog.service.auth;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.blog.service.http.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class AccessTokenServiceImpl implements AccessTokenService{
    @Value("${auth.access_token.url}")
    private String url;

    @Autowired
    private Http http;

    @Autowired
    private MetricRegistry metrics;

    @Override
    public AccessToken get(String accessTokenId) throws AccessTokenException {
        Timer.Context timer = metrics.timer(Constants.METRIC_AUTH_DURATION).time();
        try {
            metrics.meter(Constants.METRIC_AUTH_COUNT).mark();

            AccessToken token =  http.get(new URL(String.format("%s/%s", url, accessTokenId)), AccessToken.class);
            return token;

        } catch (IOException | URISyntaxException e){
            metrics.meter(Constants.METRIC_AUTH_ERRORS).mark();

            throw new AccessTokenException("Unable to process JSON response", e);
        } finally {
            timer.stop();
        }
    }
}
