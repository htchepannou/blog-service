package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostTag;
import com.tchepannou.blog.domain.Tag;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/update_post.sql"
})
public class PostUpdateIT {
    @Value("${server.port}")
    private int port;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostTagDao postTagDao;

    private String transactionId = UUID.randomUUID().toString();

    //-- Test
    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    @Test
    public void should_update_text() throws Exception {
        final Date now = new Date ();

        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus(Post.Status.published.name());
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("sample title");
        req.setUserId(110L);

        // @formatter:off
        int id = given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post/1000")
            .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1000))
                .body("blogId", is(100))
                .body("userId", is(101))
                .body("title", is("sample title"))
                .body("slug", is("sample slug"))
                .body("content", is("<div>hello world</div>"))
                .body("status", is("published"))
                .body("created", notNullValue())
                .body("updated", notNullValue())
                .body("published", nullValue())
                .body("tags", hasItems("tag1", "tag2", "tag3"))
            .extract()
                .path("id");
        ;
        // @formatter:on

        /* tags */
        List<Tag> tags = tagDao.findByNames(Arrays.asList("tag1", "tag2", "tag3"));
        assertThat(tags).hasSize(3);

        List<PostTag> postTags = postTagDao.findByPost(id);
        assertThat(postTags).hasSize(3);

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(100);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(1000);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_UPDATE_POST);
    }

    @Test
    public void should_return_400_with_empty_title() throws Exception {
        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus(Post.Status.draft.name());
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("");
        req.setUserId(100L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post/1000")
            .then()
                .log().all()
                .statusCode(400)
                .body("code", is(400))
                .body("text", is("title_empty"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_400_with_bad_status() throws Exception {
        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("????");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");
        req.setUserId(100L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post/1000")
            .then()
                .log().all()
                .statusCode(400)
                .body("code", is(400))
                .body("text", is("status_invalid"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_404_when_invalid_id() throws Exception {

        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("draft");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");
        req.setUserId(100L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post/999")
            .then()
                .log().all()
                .statusCode(404)
                .body("code", is(404))
                .body("text", is("not_found"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_404_when_invalid_blog_id() throws Exception {

        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("draft");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");
        req.setUserId(100L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/99999/post/1000")
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

        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("draft");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");
        req.setUserId(100L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/400/post/4000")
            .then()
                .log().all()
                .statusCode(404)
                .body("code", is(404))
                .body("text", is("not_found"))
        ;
        // @formatter:on

    }
}
