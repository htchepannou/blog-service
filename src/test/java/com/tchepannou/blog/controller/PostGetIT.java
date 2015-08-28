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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/get_post.sql"
})
public class PostGetIT {
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
        when()
            .get("/v1/blog/9998/post/9998")
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

            .body("attachments", hasSize(2))
            .body("attachments[0].id", is(1100))
            .body("attachments[0].name", is("video1"))
            .body("attachments[0].description", is("this is a video"))
            .body("attachments[0].thumbnailUrl", is("http://www.img.com/1100_thumb.png"))
            .body("attachments[0].oembed", is(false))
            .body("attachments[0].contentType", is("movie/quick-time"))
            .body("attachments[0].contentLength", is(143043))
            .body("attachments[0].durationSeconds", is(30))
            .body("attachments[0].width", is(0))
            .body("attachments[0].height", is(0))
                
            .body("attachments[1].id", is(1101))
            .body("attachments[1].name", is("image1"))
            .body("attachments[1].description", is("this is an image"))
            .body("attachments[1].url", is("http://www.img.com/1101.png"))
            .body("attachments[1].thumbnailUrl", is("http://www.img.com/1101_thumb.png"))
            .body("attachments[1].oembed", is(false))
            .body("attachments[1].contentType", is("image/png"))
            .body("attachments[1].contentLength", is(430394))
            .body("attachments[1].durationSeconds", is(0))
            .body("attachments[1].width", is(120))
            .body("attachments[1].height", is(144))
        ;
        // @formatter:on
    }



}
