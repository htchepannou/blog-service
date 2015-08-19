package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Header;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.auth.AuthServer;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/update_text.sql"
})
public class DeletePostIT {
    @Value("${server.port}")
    private int port;

    @Value("${auth.access_token.port}")
    private int authServerPort;

    private AuthServer authServer;
    
    @Autowired
    private PostDao postDao;

    @Autowired
    private PostEntryDao postEntryDao;

    //-- Test
    @Before
    public void setUp (){
        RestAssured.port = port;
        authServer = new AuthServer();
    }

    @Test
    public void should_delete_post() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            // @formatter:off
            given()
                    .header(new Header("access_token", "_token_"))
                .when()
                    .delete("/v1/blog/100/post/1000")
                .then()
                    .log().all()
                    .statusCode(200)
            ;
            // @formatter:on

            /* check DB */
            try {
                postDao.findById(1000);
                fail("not deleted");
            } catch (EmptyResultDataAccessException e){

            }

            List<PostEntry> entries = postEntryDao.findByPost(1000);
            assertThat(entries).isEmpty();
        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_delete_repost() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            // @formatter:off
            given()
                    .header(new Header("access_token", "_token_"))
                .when()
                    .delete("/v1/blog/100/post/2000")
                .then()
                    .log().all()
                    .statusCode(200)
            ;
            // @formatter:on

            /* check DB */
            Post post = postDao.findById(2000);
            assertThat(post).isNotNull();

            List<PostEntry> entries = postEntryDao.findByPost(2000);
            assertThat(entries).hasSize(1);
        } finally {
            authServer.stop();
        }
    }


    @Test
    public void should_return_401_when_not_authenticated() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {

            // @formatter:off
            given()
                    .header(new Header("access_token", "????"))
                .when()
                    .delete("/v1/blog/100/post/2000")
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
    public void should_return_401_when_not_auth_server_down() throws Exception {
        // @formatter:off
        given()
                .header(new Header("access_token", "????"))
            .when()
                .delete("/v1/blog/100/post/2000")
            .then()
                .log().all()
                .statusCode(401)
                .body("code", is(401))
                .body("text", is("auth_failed"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_404_when_invalid_blog_id() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            // @formatter:off
            given()
                    .header(new Header("access_token", "_token_"))
                .when()
                    .delete("/v1/blog/9999/post/2000")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", is(404))
                    .body("text", is("not_found"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_return_404_when_deleted() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            // @formatter:off
            given()
                    .header(new Header("access_token", "_token_"))
                .when()
                    .delete("/v1/blog/100/post/3000")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("code", is(404))
                    .body("text", is("not_found"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }
}
