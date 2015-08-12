package com.tchepannou.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tchepannou.blog.service.GreetingService;
import com.tchepannou.blog.service.impl.GreetingServiceImpl;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Bean
    GreetingService greetingService (){
        return new GreetingServiceImpl();
    }
}
