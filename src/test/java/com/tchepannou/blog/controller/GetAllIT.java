package com.tchepannou.blog.controller;

import com.tchepannou.blog.Starter;
import com.tchepannou.blog.client.v1.SearchRequest;
import com.tchepannou.core.http.Http;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Starter.class)
@WebIntegrationTest
@Sql({
        "/db/clean.sql",
        "/db/search.sql"
})
public class GetAllIT extends AbstractPostIT{
    private String transactionId = UUID.randomUUID().toString();

    //-- Test
    @Test
    public void should_returns_all (){
        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .get("/v1/blog/100+101+102/posts" )
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("posts", hasSize(4))

            .body("posts[0].id", is(1011))
            .body("posts[0].blogId", is(100))
            .body("posts[0].userId", is(102))
            .body("posts[0].title", is("title1011"))
            .body("posts[0].slug", is("slug1011"))
            .body("posts[0].content", is("<div>content1011</div>"))
            .body("posts[0].status", is("draft"))
            .body("posts[0].created", notNullValue())
            .body("posts[0].updated", notNullValue())
            .body("posts[0].published", notNullValue())
            .body("posts[0].tags", hasItems("tag1", "tag2"))
            .body("posts[0].attachmentIds", nullValue())
        ;
        // @formatter:on
    }


    @Test
    public void should_returns_published (){
        SearchRequest request = new SearchRequest();
        request.setStatus("published");
        request.addBlogId(100);
        request.addBlogId(101);
        request.addBlogId(102);

        // @formatter:off
        given()
                .header(Http.HEADER_TRANSACTION_ID, transactionId)
        .when()
            .get("/v1/blog/100+101+102/posts/published" )
        .then()
            .log()
                .all()
            .statusCode(HttpStatus.SC_OK)
            .body("posts", hasSize(2))
                
            .body("posts[0].id", is(1001))
            .body("posts[0].blogId", is(100))
            .body("posts[0].userId", is(101))
            .body("posts[0].title", is("title1001"))
            .body("posts[0].slug", is("slug1001"))
            .body("posts[0].content", is("<div>content1001</div>"))
            .body("posts[0].status", is("published"))
            .body("posts[0].created", notNullValue())
            .body("posts[0].updated", notNullValue())
            .body("posts[0].published", notNullValue())
            .body("posts[0].tags", hasSize(1))
            .body("posts[0].tags", hasItems("tag4"))
            .body("posts[0].attachmentIds", hasItems(1101))
        ;
        // @formatter:on
    }    
}
