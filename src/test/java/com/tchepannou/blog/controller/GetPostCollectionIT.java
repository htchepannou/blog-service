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

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/get_post_collection.sql"
})
public class GetPostCollectionIT {
    @Value("${server.port}")
    private int port;

    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    //-- Test
    @Test
    public void should_returns_empty_for_invalid_blog (){
        // @formatter:off
        when()
            .get("/blog/v1/posts/99999" )
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("posts", hasSize(0))
        ;
        // @formatter:on
    }

    @Test
    public void should_returns_collection (){
        // @formatter:off
        when()
            .get("/blog/v1/posts/100" )
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("posts", hasSize(3))

            .body("posts[0].id", is(1011))
            .body("posts[0].blogId", is(101))
            .body("posts[0].title", is("title1011"))
            .body("posts[0].slug", is("slug1011"))
            .body("posts[0].content", is("<div>content1011</div>"))
            .body("posts[0].type", is("url"))
            .body("posts[0].status", is("draft"))
            .body("posts[0].created", notNullValue())
            .body("posts[0].updated", notNullValue())
            .body("posts[0].published", notNullValue())

            .body("posts[1].id", is(1002))
            .body("posts[1].blogId", is(100))
            .body("posts[1].title", is("title1002"))
            .body("posts[1].slug", is("slug1002"))
            .body("posts[1].content", is("<div>content1002</div>"))
            .body("posts[1].type", is("url"))
            .body("posts[1].status", is("draft"))
            .body("posts[1].created", notNullValue())
            .body("posts[1].updated", notNullValue())
            .body("posts[1].published", notNullValue())

            .body("posts[2].id", is(1000))
            .body("posts[2].blogId", is(100))
            .body("posts[2].title", is("title1000"))
            .body("posts[2].slug", is("slug1000"))
            .body("posts[2].content", is("<div>content1000</div>"))
            .body("posts[2].type", is("text"))
            .body("posts[2].status", is("published"))
            .body("posts[2].created", notNullValue())
            .body("posts[2].updated", notNullValue())
            .body("posts[2].published", notNullValue())
        ;
        // @formatter:on
    }

    @Test
    public void should_returns_collection_limit3_offset0 (){
        // @formatter:off
        given()
            .param("limit", "3")
            .param("offset", "0")
        .when()
            .get("/blog/v1/posts/200" )
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("posts", hasSize(3))

            .body("posts.id", contains(2005, 2004, 2003))
        ;
        // @formatter:on
    }

    @Test
    public void should_returns_collection_limit3_offset3 (){
        // @formatter:off
        given()
            .param("limit", "3")
            .param("offset", "3")
        .when()
            .get("/blog/v1/posts/200" )
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("posts", hasSize(2))

            .body("posts.id", contains(2002, 2001))
        ;
        // @formatter:on
    }
}
