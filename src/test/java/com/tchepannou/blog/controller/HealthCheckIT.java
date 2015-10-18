package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.blog.Starter;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
public class HealthCheckIT {
    @Value ("${server.port}")
    private int port;

    @Before
    public final void setUp() {
        RestAssured.port = port;
    }

    //-- Tests
    @Test
    public void test_status (){

        // @formatter:off
        when()
            .get("/health")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("status", is("UP"))
        ;
        // @formatter:on
    }
}
