package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.dao.EventLogDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.domain.EventLog;
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

import static com.jayway.restassured.RestAssured.when;
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

    @Autowired
    private EventLogDao eventLogDao;

    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    @Test
    public void should_delete_post() throws Exception {
        // @formatter:off
        when()
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

        /* events */
        List<EventLog> events = eventLogDao.findByPost(1000, 100, 0);
        assertThat(events).hasSize(1);

        EventLog event = events.get(0);
        assertThat(event.getBlogId()).isEqualTo(100);
        assertThat(event.getCreated()).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getName()).isEqualTo(Constants.EVENT_DELETE_POST);
        assertThat(event.getPostId()).isEqualTo(1000);
        assertThat(event.getUserId()).isEqualTo(0);
        assertThat(event.getRequest()).isNull();
    }

    @Test
    public void should_delete_post_as_owner() throws Exception {
        // @formatter:off
        when()
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

        /* events */
        List<EventLog> events = eventLogDao.findByPost(1000, 100, 0);
        assertThat(events).hasSize(1);

        EventLog event = events.get(0);
        assertThat(event.getBlogId()).isEqualTo(100);
        assertThat(event.getCreated()).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getName()).isEqualTo(Constants.EVENT_DELETE_POST);
        assertThat(event.getPostId()).isEqualTo(1000);
        assertThat(event.getUserId()).isEqualTo(0);
        assertThat(event.getRequest()).isNull();
    }

    @Test
    public void should_delete_repost() throws Exception {
        // @formatter:off
        when()
            .delete("/v1/blog/100/post/2000")
        .then()
            .log().all()
            .statusCode(200)
        ;
        // @formatter:on

        /* post */
        Post post = postDao.findById(2000);
        assertThat(post).isNotNull();

        /* entries */
        List<PostEntry> entries = postEntryDao.findByPost(2000);
        assertThat(entries).hasSize(1);

        /* events */
        List<EventLog> events = eventLogDao.findByPost(2000, 100, 0);
        assertThat(events).hasSize(1);

        EventLog event = events.get(0);
        assertThat(event.getBlogId()).isEqualTo(100);
        assertThat(event.getCreated()).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getName()).isEqualTo(Constants.EVENT_DELETE_POST);
        assertThat(event.getPostId()).isEqualTo(2000);
        assertThat(event.getUserId()).isEqualTo(0);
        assertThat(event.getRequest()).isNull();
    }


    @Test
    public void should_return_404_when_invalid_blog_id() throws Exception {

        // @formatter:off
        when()
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
        when()
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
