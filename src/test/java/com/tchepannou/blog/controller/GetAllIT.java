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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
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
            .get("/v1/blog/100,101,102" )
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
            .body("posts[0].attachments", hasSize(0))
            .body("posts[0].mainAttachmentId", nullValue())
                
            .body("posts[1].id", is(1002))
            .body("posts[1].blogId", is(100))
            .body("posts[1].userId", is(101))
            .body("posts[1].title", is("title1002"))
            .body("posts[1].slug", is("slug1002"))
            .body("posts[1].content", is("<div>content1002</div>"))
            .body("posts[1].status", is("draft"))
            .body("posts[1].created", notNullValue())
            .body("posts[1].updated", notNullValue())
            .body("posts[1].published", notNullValue())
            .body("posts[1].tags", hasItems("tag2", "tag3"))
            .body("posts[1].attachments", hasSize(2))
            .body("posts[1].mainAttachmentId", nullValue())

            .body("posts[3].id", is(1000))
            .body("posts[3].blogId", is(100))
            .body("posts[3].userId", is(101))
            .body("posts[3].title", is("title1000"))
            .body("posts[3].slug", is("slug1000"))
            .body("posts[3].content", is("<div>content1000</div>"))
            .body("posts[3].status", is("published"))
            .body("posts[3].created", notNullValue())
            .body("posts[3].updated", notNullValue())
            .body("posts[3].published", notNullValue())
            .body("posts[3].tags", hasSize(0))
            .body("posts[3].attachments", hasSize(2))
            .body("posts[3].mainAttachmentId", is(1100))
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
            .get("/v1/blog/100,101,102/published" )
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
            .body("posts[0].attachments", hasSize(1))
            .body("posts[0].mainAttachmentId", nullValue())

            .body("posts[1].id", is(1000))
            .body("posts[1].blogId", is(100))
            .body("posts[1].userId", is(101))
            .body("posts[1].title", is("title1000"))
            .body("posts[1].slug", is("slug1000"))
            .body("posts[1].content", is("<div>content1000</div>"))
            .body("posts[1].status", is("published"))
            .body("posts[1].created", notNullValue())
            .body("posts[1].updated", notNullValue())
            .body("posts[1].published", notNullValue())
            .body("posts[1].tags", hasSize(0))
            .body("posts[1].attachments", hasSize(2))
            .body("posts[1].mainAttachmentId", is(1100))
        ;
        // @formatter:on
    }    
}
