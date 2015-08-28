package com.tchepannou.blog.jms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {

    @Bean PostEventReceiver eventLogReceiver(){
        return new PostEventReceiver();
    }
}
