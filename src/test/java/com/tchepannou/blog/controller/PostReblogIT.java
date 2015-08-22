package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.auth.AuthServer;
import com.tchepannou.blog.dao.EventLogDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.EventLog;
import com.tchepannou.blog.domain.PostEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/reblog_text.sql"
})
public class PostReblogIT {
    @Value("${server.port}")
    private int port;

    @Value("${auth.access_token.port}")
    private int authServerPort;

    private AuthServer authServer;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostEntryDao postEntryDao;

    @Autowired
    private EventLogDao eventLogDao;

    //-- Test
    @Before
    public void setUp (){
        RestAssured.port = port;
        authServer = new AuthServer();
    }

    @Test
    public void should_reblog_new_post() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 110, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/v1/blog/1/post/1000/reblog")
                .then()
                    .log().all()
                    .statusCode(201)
            ;
            // @formatter:on

            /* post entry */
            List<PostEntry> entries = postEntryDao.findByPost(1000);
            assertThat(entries).hasSize(2);

            /* events */
            Thread.sleep(1000);
            List<EventLog> events = eventLogDao.findByPost(1000, 100, 0);
            assertThat(events).hasSize(1);

            EventLog event = events.get(0);
            assertThat(event.getBlogId()).isEqualTo(1);
            assertThat(event.getCreated()).isNotNull();
            assertThat(event.getId()).isGreaterThan(0);
            assertThat(event.getName()).isEqualTo(Constants.EVENT_REBLOG_POST);
            assertThat(event.getPostId()).isEqualTo(1000);
            assertThat(event.getUserId()).isEqualTo(110);
            assertThat(event.getRequest()).isNull();

        } finally {
            authServer.stop();
        }
    }


    @Test
    public void should_reblog_existing_post() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 110, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/v1/blog/200/post/2000/reblog")
                .then()
                    .log().all()
                    .statusCode(200)
            ;
            // @formatter:on

            /* post entry */
            List<PostEntry> entries = postEntryDao.findByPost(1000);
            assertThat(entries).hasSize(1);

            /* events */
            List<EventLog> events = eventLogDao.findByPost(2000, 200, 0);
            assertThat(events).hasSize(0);

        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_return_401_when_invalid_token() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 110, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .header(new Header("access_token", "???"))
                .when()
                    .post("/v1/blog/1/post/1000/reblog")
                .then()
                    .log().all()
                    .statusCode(401)
            ;
            // @formatter:on

        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_return_401_when_not_logged_in() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 110, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .header(new Header("access_token", null))
                .when()
                    .post("/v1/blog/1/post/1000/reblog")
                .then()
                    .log().all()
                    .statusCode(401)
            ;
            // @formatter:on

        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_return_403_when_bad_permission() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 110, Arrays.asList(Constants.PERMISSION_EDIT)));
        try {
            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/v1/blog/1/post/1000/reblog")
                .then()
                    .log().all()
                    .statusCode(403)
            ;
            // @formatter:on

        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_return_404_when_not_found() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 110, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/v1/blog/1/post/999999/reblog")
                .then()
                    .log().all()
                    .statusCode(404)
            ;
            // @formatter:on

        } finally {
            authServer.stop();
        }
    }
}
