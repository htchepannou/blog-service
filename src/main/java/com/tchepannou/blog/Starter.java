package com.tchepannou.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class Starter {
    //-- Main
    public static void main (String [] args){
        SpringApplication.run(Starter.class, args);
    }
}
