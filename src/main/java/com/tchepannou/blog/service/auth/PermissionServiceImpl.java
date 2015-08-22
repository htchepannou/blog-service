package com.tchepannou.blog.service.auth;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.blog.service.http.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URL;

public class PermissionServiceImpl implements PermissionService {
    @Value("${auth.permission.url}")
    private String url;

    @Autowired
    private Http http;

    @Autowired
    private MetricRegistry metrics;

    @Override
    public PermissionCollection get (long blogId, long userId) {
        Timer.Context timer = metrics.timer(Constants.METRIC_PERMISSION_DURATION).time();
        try {
            metrics.meter(Constants.METRIC_PERMISSION_COUNT).mark();

            return http.get(new URL(String.format("%s/user/%s/space/%s/app/blog", url, userId, blogId)), PermissionCollection.class);

        } catch (IOException e){
            metrics.meter(Constants.METRIC_PERMISSION_ERRORS).mark();

            throw new AccessTokenException("Unable to process JSON response", e);
        } finally {
            timer.stop();
        }
    }
}
