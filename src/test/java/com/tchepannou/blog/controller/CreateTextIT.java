package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Header;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.auth.AuthServer;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.rr.CreateTextRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/create_text.sql"
})
public class CreateTextIT {
    @Value("${server.port}")
    private int port;

    @Value("${auth.access_token.port}")
    private int authServerPort;

    private AuthServer authServer;

    @Before
    public void setUp (){
        RestAssured.port = port;
        authServer = new AuthServer();
    }


    //-- Test
    @Test
    public void should_create_text() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            CreateTextRequest req = new CreateTextRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus(Post.Status.draft.name());
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("sample title");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", greaterThan(0))
                    .body("blogId", is(100))
                    .body("userId", is(101))
                    .body("title", is("sample title"))
                    .body("slug", is("sample slug"))
                    .body("content", is("<div>hello world</div>"))
                    .body("type", is("text"))
                    .body("status", is("draft"))
                    .body("created", notNullValue())
                    .body("updated", notNullValue())
                    .body("published", nullValue())
                    .body("tags", hasItems("tag1", "tag2", "tag3"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }


    @Test
    public void should_fail_when_not_authenticated() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            CreateTextRequest req = new CreateTextRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus("draft");
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("test");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header("access_token", "????"))
                .when()
                    .post("/blog/v1/posts/100/text")
                .then()
                    .log().all()
                    .statusCode(401)
                    .body("code", is(401))
                    .body("text", is("auth_failed"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_fail_when_not_auth_server_down() throws Exception {
        CreateTextRequest req = new CreateTextRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("draft");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(new Header("access_token", "????"))
            .when()
                .post("/blog/v1/posts/100/text")
            .then()
                .log().all()
                .statusCode(401)
                .body("code", is(401))
                .body("text", is("auth_failed"))
        ;
        // @formatter:on
    }

    @Test
    public void should_fail_with_empty_title() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            CreateTextRequest req = new CreateTextRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus(Post.Status.draft.name());
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", is(400))
                    .body("text", is("title_empty"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_fail_with_bad_status() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            CreateTextRequest req = new CreateTextRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus("????");
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("test");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("code", is(400))
                    .body("text", is("status_invalid"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }

}