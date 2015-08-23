package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.tchepannou.blog.Starter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
public class HealthCheckIT extends AbstractHandler {
    @Value ("${server.port}")
    private int port;

    @Value("${auth.port}")
    private int authPort;

    private Server auth;


    @Before
    public void setUp () throws Exception{
        RestAssured.port = port;

        auth = new Server(authPort);
        auth.setHandler(this);
        auth.start();
    }

    @After
    public void tearDown() throws Exception {
        auth.stop();
    }


    //-- AbstractHandler overrides
    @Override
    public void handle(String s, Request request, HttpServletRequest servletRquest, HttpServletResponse servletResponse)
            throws IOException, ServletException {
        servletResponse.setContentType("application/json");
        servletResponse.setStatus(HttpServletResponse.SC_OK);

        servletResponse.getWriter().print("{\"status\":\"UP\"}");

        request.setHandled(true);
    }


    //-- Tests
    @Test
    public void test_status (){

        // @formatter:off
        when()
            .get("/health")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .log()
                .all()
            .body("status", is("UP"))
        ;
        // @formatter:on
    }
}
