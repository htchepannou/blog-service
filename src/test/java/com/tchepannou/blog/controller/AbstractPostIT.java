package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.blog.jms.PostEventReceiver;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractPostIT {
    @Value("${server.port}")
    private int port;

    @Before
    public final void setUp() {
        RestAssured.port = port;
        PostEventReceiver.lastEvent = null;
    }


}
