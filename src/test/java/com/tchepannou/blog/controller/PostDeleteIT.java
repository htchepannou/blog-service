package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.dao.PostDao;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/delete_post.sql"
})
public class PostDeleteIT {
    @Value("${server.port}")
    private int port;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostEntryDao postEntryDao;

    private String transactionId = UUID.randomUUID().toString();

    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    @Test
    public void should_delete_post() throws Exception {
        final Date now = new Date();

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .delete("/v1/blog/100/post/1000")
        .then()
            .log().all()
            .statusCode(200)
        ;
        // @formatter:on

        /* post */
        try {
            postDao.findById(1000);
            fail("not deleted");
        } catch (EmptyResultDataAccessException e){

        }

        /* entries */
        List<PostEntry> entries = postEntryDao.findByPost(1000);
        assertThat(entries).isEmpty();

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(100);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(1000);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_DELETE_POST);
    }

    @Test
    public void should_delete_post_as_owner() throws Exception {
        final Date now = new Date();

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .delete("/v1/blog/100/post/1000")
        .then()
            .log().all()
            .statusCode(200)
        ;
        // @formatter:on

        /* post */
        try {
            postDao.findById(1000);
            fail("not deleted");
        } catch (EmptyResultDataAccessException e){

        }

        /* entries */
        List<PostEntry> entries = postEntryDao.findByPost(1000);
        assertThat(entries).isEmpty();

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(100);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(1000);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_DELETE_POST);
    }

    @Test
    public void should_delete_repost() throws Exception {
        final Date now = new Date ();

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .delete("/v1/blog/100/post/2000")
        .then()
            .log().all()
            .statusCode(200)
        ;
        // @formatter:on

        /* post */
        try {
            postDao.findById(2000);
            fail("");
        } catch (EmptyResultDataAccessException e){

        }

        /* entries */
        List<PostEntry> entries = postEntryDao.findByPost(2000);
        assertThat(entries).hasSize(1);

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(100);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(2000);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_DELETE_POST);
    }


    @Test
    public void should_return_404_when_invalid_blog_id() throws Exception {

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .delete("/v1/blog/9999/post/2000")
        .then()
            .log().all()
            .statusCode(404)
            .body("code", is(404))
            .body("text", is("not_found"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_404_when_deleted() throws Exception {
        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .delete("/v1/blog/400/post/4000")
        .then()
            .log().all()
            .statusCode(404)
            .body("code", is(404))
            .body("text", is("not_found"))
        ;
        // @formatter:on
    }
}
