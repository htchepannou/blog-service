package com.tchepannou.blog.controller;

import com.tchepannou.blog.rr.CreateTextRequest;
import com.tchepannou.blog.rr.PostCollectionResponse;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.rr.UpdateTextRequest;
import com.tchepannou.blog.service.GetPostListService;
import com.tchepannou.blog.service.GetPostService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;

@RestController
@Api(basePath = "/blog/v1", value = "Blog API", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value="/blog/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class BlogController {
    //-- Atributes
    @Autowired
    GetPostService getPostService;

    @Autowired
    GetPostListService getPostListService;

    //-- REST methods
    @RequestMapping(method = RequestMethod.GET, value="/post/{id}")
    @ApiOperation(value="Returns a post", notes = "Return a post by its ID")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found")
    })
    public PostResponse get(@PathVariable long id) {
        return getPostService.execute(id);
    }

    @RequestMapping(method = RequestMethod.GET, value="/posts/{bid}")
    @ApiOperation(value="List posts", notes = "Return a post by its ID")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found")
    })
    public PostCollectionResponse list(
            @PathVariable long bid,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value="offset", defaultValue = "0") int offset
    ) {
        return getPostListService.execute(
                new GetPostListService.Request(bid, limit, offset)
        );
    }



    @RequestMapping(method = {RequestMethod.POST, RequestMethod.POST}, value="/posts/{bid}/text")
    @ApiOperation(value="Create a new Text")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to delete the post.")
    })
    public PostResponse createText(
            @HeaderParam(value="access_token") String accessToken,
            @PathVariable long bid,
            @RequestBody @Valid CreateTextRequest request
    ) {
        return new PostResponse();
    }

    @RequestMapping(method = RequestMethod.POST, value="/posts/{bid}/text/{id}")
    @ApiOperation(value="Update a Text")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to delete the post.")
    })
    public PostResponse updateText(
            @HeaderParam(value="access_token") String accessToken,
            @PathVariable long bid,
            @RequestBody @Valid UpdateTextRequest request
    ) {
        return new PostResponse();
    }


    @RequestMapping(method = RequestMethod.DELETE, value="/{bid}/post/{id}")
    @ApiOperation(value="Delete a post", notes = "Delete a post")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to delete the post.")
    })
    public PostResponse delete(
            @HeaderParam(value="access_token") String accessToken,
            @PathVariable long bid,
            @PathVariable long id
    ) {
        return new PostResponse();
    }


    //-- Exception Handler
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public void notFound() {    // NOSONAR
    }
}
