package com.tchepannou.blog.controller;

import com.tchepannou.blog.Starter;
import com.tchepannou.core.http.Http;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
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
public class PostGetIT extends AbstractPostIT{
    private String transactionId = UUID.randomUUID().toString();

    //-- Test
    @Test
    public void should_returns_404_for_invalid_id (){
        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .get("/v1/blog/100/post/9999")
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
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .get("/v1/blog/9998/post/9998")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
        // @formatter:on
    }

    @Test
    public void should_returns_post (){
        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .get("/v1/blog/100/post/1000")
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("id", is(1000))
            .body("blogId", is(100))
            .body("userId", is(101))
            .body("title", is("sample title"))
            .body("slug", is("sample slug"))
            .body("content", is("<div>This is the content</div>"))
            .body("status", is("published"))
            .body("created", notNullValue())
            .body("updated", notNullValue())
            .body("published", notNullValue())
            .body("tags", hasItems("tag4", "tag3", "tag2"))
            .body("attachmentIds", hasItems(1100, 1101))
        ;
        // @formatter:on
    }



}
