package com.tchepannou.blog.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.Header;
import com.tchepannou.blog.Starter;
import com.tchepannou.blog.auth.AuthServer;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.PostEntryDao;
import com.tchepannou.blog.dao.PostTagDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.PostTag;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.rr.CreateTextRequest;
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

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/update_text.sql"
})
public class UpdateTextIT {
    @Value("${server.port}")
    private int port;

    @Value("${auth.access_token.port}")
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

    //-- Test
    @Before
    public void setUp (){
        RestAssured.port = port;
        authServer = new AuthServer();
    }

    @Test
    public void should_update_text() throws Exception {
        authServer.start(authServerPort, new AuthServer.OKHandler("_token_", 101));
        try {
            CreateTextRequest req = new CreateTextRequest();
            req.setContent("<div>hello world</div>");
            req.setStatus(Post.Status.published.name());
            req.setSlug("sample slug");
            req.setTags(Arrays.asList("tag1", "tag2", "tag3"));
            req.setTitle("sample title");
            req.setPublished(new Date());

            // @formatter:off
            int id = given()
                    .contentType(ContentType.JSON)
                    .content(req, ObjectMapperType.JACKSON_2)
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text/1000")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1000))
                    .body("blogId", is(100))
                    .body("userId", is(101))
                    .body("title", is("sample title"))
                    .body("slug", is("sample slug"))
                    .body("content", is("<div>hello world</div>"))
                    .body("type", is("text"))
                    .body("status", is("published"))
                    .body("created", notNullValue())
                    .body("updated", notNullValue())
                    .body("published", notNullValue())
                    .body("tags", hasItems("tag1", "tag2", "tag3"))
                .extract()
                    .path("id");
            ;
            // @formatter:on

            /* check DB */
            List<Tag> tags = tagDao.findByNames(Arrays.asList("tag1", "tag2", "tag3"));
            assertThat(tags).hasSize(3);

            List<PostTag> postTags = postTagDao.findByPost(id);
            assertThat(postTags).hasSize(3);

        } finally {
            authServer.stop();
        }
    }
    @Test
    public void should_return_400_with_empty_title() throws Exception {
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
                    .post("/blog/v1/posts/100/text/1000")
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
                    .post("/blog/v1/posts/100/text/1000")
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
                    .post("/blog/v1/posts/100/text/1000")
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
                .post("/blog/v1/posts/100/text/1000")
            .then()
                .log().all()
                .statusCode(401)
                .body("code", is(401))
                .body("text", is("auth_failed"))
        ;
        // @formatter:on
    }

    @Test
    public void should_return_403_when_now_owner_of_blog() throws Exception {
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
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text/2000")
                .then()
                    .log().all()
                    .statusCode(403)
                    .body("code", is(403))
                    .body("text", is("invalid_blog"))
            ;
            // @formatter:on


        } finally {
            authServer.stop();
        }
    }

    @Test
    public void should_return_404_when_invalid_id() throws Exception {
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
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text/999")
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
    public void should_return_404_when_invalid_blog_id() throws Exception {
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
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/99999/text/1000")
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
                    .header(new Header("access_token", "_token_"))
                .when()
                    .post("/blog/v1/posts/100/text/3000")
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
