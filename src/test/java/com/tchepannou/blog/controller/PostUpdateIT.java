package com.tchepannou.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.dao.EventLogDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.EventLog;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostTag;
import com.tchepannou.blog.domain.Tag;
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

    @Autowired
    private EventLogDao eventLogDao;

    //-- Test
    @Before
    public void setUp (){
        RestAssured.port = port;
    }

    @Test
    public void should_update_text() throws Exception {
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

        /* events */
        List<EventLog> events = eventLogDao.findByPost(1000, 100, 0);
        assertThat(events).hasSize(1);

        EventLog event = events.get(0);
        assertThat(event.getBlogId()).isEqualTo(100);
        assertThat(event.getCreated()).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getName()).isEqualTo(Constants.EVENT_UPDATE_TEXT);
        assertThat(event.getPostId()).isEqualTo(id);
        assertThat(event.getUserId()).isEqualTo(110);

        UpdatePostRequest req2 = new ObjectMapper().readValue(event.getRequest().getBytes(), UpdatePostRequest.class);
        assertThat(req2).isEqualToComparingFieldByField(req);
    }

    @Test
    public void should_update_text_as_owner() throws Exception {
        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus(Post.Status.published.name());
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("sample title");
        req.setUserId(101L);

        // @formatter:off
        int id = given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
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

        /* events */
        List<EventLog> events = eventLogDao.findByPost(1000, 100, 0);
        assertThat(events).hasSize(1);

        EventLog event = events.get(0);
        assertThat(event.getBlogId()).isEqualTo(100);
        assertThat(event.getCreated()).isNotNull();
        assertThat(event.getId()).isGreaterThan(0);
        assertThat(event.getName()).isEqualTo(Constants.EVENT_UPDATE_TEXT);
        assertThat(event.getPostId()).isEqualTo(id);
        assertThat(event.getUserId()).isEqualTo(101);

        UpdatePostRequest req2 = new ObjectMapper().readValue(event.getRequest().getBytes(), UpdatePostRequest.class);
        assertThat(req2).isEqualToComparingFieldByField(req);
    }

    @Test
    public void should_return_400_with_empty_title() throws Exception {
        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus(Post.Status.draft.name());
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("");

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
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

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
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
