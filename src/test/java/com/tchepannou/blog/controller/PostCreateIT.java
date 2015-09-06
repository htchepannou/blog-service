package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.client.v1.BlogConstants;
import com.tchepannou.blog.client.v1.CreatePostRequest;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostEntry;
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
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/create_post.sql"
})
public class PostCreateIT {
    @Value("${server.port}")
    private int port;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostTagDao postTagDao;

    @Autowired
    private PostEntryDao postEntryDao;

    private String transactionId = UUID.randomUUID().toString();


    //-- Test
    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void should_create_post() throws Exception {
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus(Post.Status.draft.name());
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("sample title");
        req.setUserId(101L);

        final Date now = new Date ();

        // @formatter:off
        int id = given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post")
            .then()
                .log().all()
                .statusCode(201)
                .body("id", greaterThan(0))
                .body("blogId", is(100))
                .body("userId", is(101))
                .body("title", is("sample title"))
                .body("slug", is("sample slug"))
                .body("content", is("<div>hello world</div>"))
                .body("status", is("draft"))
                .body("created", notNullValue())
                .body("updated", notNullValue())
                .body("published", nullValue())
                .body("tags", hasItems("tag1", "tag2", "tag3"))
                .body("userId", is(101))
            .extract()
                .path("id");
        ;
        // @formatter:on

        /* post */
        Post post = postDao.findById(id);
        assertThat(post).isNotNull();

        /* tags */
        List<Tag> tags = tagDao.findByNames(Arrays.asList("tag1", "tag2", "tag3"));
        assertThat(tags).hasSize(3);

        List<PostTag> postTags = postTagDao.findByPost(id);
        assertThat(postTags).hasSize(3);

        /* entries */
        List<PostEntry> entries = postEntryDao.findByPost(id);
        assertThat(entries).hasSize(1);

        /* event */
        assertThat(PostEventReceiver.lastEvent.getBlogId()).isEqualTo(100);
        assertThat(PostEventReceiver.lastEvent.getDate()).isAfter(now);
        assertThat(PostEventReceiver.lastEvent.getId()).isEqualTo(id);
        assertThat(PostEventReceiver.lastEvent.getTransactionId()).isEqualTo(transactionId);
        assertThat(PostEventReceiver.lastEvent.getType()).isEqualTo(BlogConstants.EVENT_CREATE_POST);
    }

    @Test
    public void should_return_400_with_bad_status() throws Exception {
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("????");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");
        req.setUserId(101L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post")
            .then()
                .log().all()
                .statusCode(400)
                .body("code", is(400))
                .body("text", is("status_invalid"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_400_with_no_title() throws Exception {
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("draft");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("");
        req.setUserId(43L);

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
            .when()
                .post("/v1/blog/100/post")
            .then()
                .log().all()
                .statusCode(400)
                .body("code", is(400))
                .body("text", is("title_empty"))
        ;
        // @formatter:on
    }
}
