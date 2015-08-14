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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/get_post.sql"
})
public class GetPostIT {
    @Value("${server.port}")
    private int port;

    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    //-- Test
    @Test
    public void should_returns_404_for_invalid_id (){
        // @formatter:off
        when()
            .get("/blog/v1/post/9999")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
        // @formatter:on
    }

    @Test
    public void should_returns_404_for_deleted_post (){
        // @formatter:off
        when()
            .get("/blog/v1/post/9998")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
        // @formatter:on
    }

    @Test
    public void should_returns_text (){
        // @formatter:off
        when()
            .get("/blog/v1/post/1000")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("id", is(1000))
            .body("title", is("sample title"))
            .body("slug", is("sample slug"))
            .body("content", is("<div>This is the content</div>"))
            .body("type", is("text"))
            .body("status", is("published"))
            .body("created", notNullValue())
            .body("updated", notNullValue())
            .body("published", notNullValue())
            .body("tags", hasItems("tag4", "tag3", "tag2"))
        ;
        // @formatter:on
    }



}
