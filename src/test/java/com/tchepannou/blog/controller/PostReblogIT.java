package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.PostEntry;
import com.tchepannou.blog.jms.PostEventReceiver;
import com.tchepannou.core.http.Http;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/reblog_post.sql"
})
public class PostReblogIT {
    @Value("${server.port}")
    private int port;

    @Autowired
    private PostEntryDao postEntryDao;

    private String transactionId = UUID.randomUUID().toString();

    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    @Test
    public void should_reblog_new_post() throws Exception {
        final Date now = new Date ();

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
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

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(1);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(1000);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_REBLOG_POST);
    }


    @Test
    public void should_reblog_existing_post() throws Exception {
        final Date now = new Date ();

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
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

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(200);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(2000);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_REBLOG_POST);
    }


    @Test
    public void should_return_404_when_not_found() throws Exception {

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .post("/v1/blog/1/post/999999/reblog")
        .then()
            .log().all()
            .statusCode(404)
        ;
        // @formatter:on

    }
}
