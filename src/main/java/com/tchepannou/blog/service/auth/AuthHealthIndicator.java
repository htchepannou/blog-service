package com.tchepannou.blog.service.auth;

import com.tchepannou.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.Map;

public class AuthHealthIndicator implements HealthIndicator {
    private static final Logger LOG = LoggerFactory.getLogger(AuthHealthIndicator.class);

    @Value("${auth.hostname}")
    private String hostname;

    @Value("${auth.port}")
    private int port;

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;

    //-- HealthIndicator overrides
    @Override
    public Health health() {
        try {
            Map result = new Http()
                    .withHost(hostname)
                    .withPort(port)
                    .withPath("/health")
                    .withObjectMapper(jackson.build())
                    .get(Map.class);

            return "UP".equals(result.get("status"))
                    ? Health.up().build()
                    : Health.down().build()
            ;
        } catch (IOException e){
            LOG.error("Health check of auth-service failed", e);
            return Health
                    .down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
