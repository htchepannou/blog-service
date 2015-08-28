package com.tchepannou.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Header;
import com.tchepannou.blog.Constants;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.auth.AuthServer;
import com.tchepannou.blog.client.v1.CreatePostRequest;
import com.tchepannou.blog.dao.EventLogDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.EventLog;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostEntry;
import com.tchepannou.blog.domain.PostTag;
import com.tchepannou.blog.domain.Tag;
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
import java.util.List;

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

    @Value("${auth.port}")
    private int authServerPort;

    private AuthServer authServer;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostTagDao postTagDao;

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
    public void should_create_text() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            CreatePostRequest req = new CreatePostRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus(Post.Status.draft.name());
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("sample title");

            // @formatter:off
            int id = given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header(Http.HEADER_ACCESS_TOKEN, "_token_"))
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

            /* events */
            List<EventLog> events = eventLogDao.findByPost(id, 1000, 0);
            assertThat(events).hasSize(1);
            
            EventLog event = events.get(0);
            assertThat(event.getBlogId()).isEqualTo(100);
            assertThat(event.getCreated()).isNotNull();
            assertThat(event.getId()).isGreaterThan(0);
            assertThat(event.getName()).isEqualTo(Constants.EVENT_CREATE_TEXT);
            assertThat(event.getPostId()).isEqualTo(id);
            assertThat(event.getUserId()).isEqualTo(101);

            CreatePostRequest req2 = new ObjectMapper().readValue(event.getRequest().getBytes(), CreatePostRequest.class);
            assertThat(req2).isEqualToComparingFieldByField(req);

        } finally {
            authServer.stop();
        }
    }


    @Test
    public void should_return_400_with_empty_title() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            CreatePostRequest req = new CreatePostRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus(Post.Status.draft.name());
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header(Http.HEADER_ACCESS_TOKEN, "_token_"))
                .when()
                    .post("/v1/blog/100/post")
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
    public void should_return_400_with_bad_status() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            CreatePostRequest req = new CreatePostRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus("????");
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("test");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header(Http.HEADER_ACCESS_TOKEN, "_token_"))
                .when()
                    .post("/v1/blog/100/post")
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

    @Test
    public void should_return_401_when_not_authenticated() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101, Arrays.asList(Constants.PERMISSION_CREATE)));
        try {
            CreatePostRequest req = new CreatePostRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus("draft");
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("test");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header(Http.HEADER_ACCESS_TOKEN, "????"))
                .when()
                    .post("/v1/blog/100/post")
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
        CreatePostRequest req = new CreatePostRequest();
        req.setContent("<div>hello world</div>");
        req.setStatus("draft");
        req.setSlug("sample slug");
        req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        req.setTitle("test");

        // @formatter:off
        given()
                .contentType(ContentType.JSON)
                .content(req, ObjectMapperType.JACKSON_2)
                .header(new Header(Http.HEADER_ACCESS_TOKEN, "????"))
            .when()
                .post("/v1/blog/100/post")
            .then()
                .log().all()
                .statusCode(401)
                .body("code", is(401))
                .body("text", is("auth_failed"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_403_when_bad_permission() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101, Arrays.asList(Constants.PERMISSION_DELETE, Constants.PERMISSION_EDIT)));
        try {
            CreatePostRequest req = new CreatePostRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus("draft");
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("test");

            // @formatter:off
            given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header(Http.HEADER_ACCESS_TOKEN, "_token_"))
                .when()
                    .post("/v1/blog/100/post")
                .then()
                    .log().all()
                    .statusCode(403)
                    .body("code", is(403))
                    .body("text", is("bad_permission"))
            ;
            // @formatter:on

        } finally {
            authServer.stop();
        }
    }
}
