package com.tchepannou.blog.service.auth;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.tchepannou.auth.client.v1.PermissionCollectionResponse;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.core.http.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;


public class PermissionServiceImpl implements PermissionService {
    @Value("${auth.hostname}")
    private String hostname;

    @Value("${auth.port}")
    private int port;

    @Value("${auth.permission.path}")
    private String path;

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    @Autowired
    private MetricRegistry metrics;

    @Override
    public PermissionCollectionResponse get (long blogId, long userId) {
        Timer.Context timer = metrics.timer(Constants.METRIC_PERMISSION_DURATION).time();
        try {
            metrics.meter(Constants.METRIC_PERMISSION_COUNT).mark();

            return http()
                    .withPath(String.format("%s/user/%d/space/%d/app/blog", path, userId, blogId))
                    .get(PermissionCollectionResponse.class);

        } catch (IOException e){
            metrics.meter(Constants.METRIC_PERMISSION_ERRORS).mark();

            throw new AccessTokenException("Unable to process JSON response", e);
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
