package com.tchepannou.blog.controller;

import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.blog.exception.AuthorizationException;
import com.tchepannou.blog.exception.DuplicatePostException;
import com.tchepannou.blog.client.v1.CreateTextRequest;
import com.tchepannou.blog.client.v1.ErrorResponse;
import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdateTextRequest;
import com.tchepannou.blog.service.CreateTextCommand;
import com.tchepannou.blog.service.DeletePostCommand;
import com.tchepannou.blog.service.GetPostCommand;
import com.tchepannou.blog.service.GetPostListCommand;
import com.tchepannou.blog.service.ReblogPostCommand;
import com.tchepannou.blog.service.UpdateTextCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(basePath = "/v1/blog", value = "Blog API", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value="/v1/blog", produces = MediaType.APPLICATION_JSON_VALUE)
public class BlogController {

    //-- Atributes
    private static final Logger LOG = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    GetPostCommand getPostService;

    @Autowired
    GetPostListCommand getPostListService;

    @Autowired
    CreateTextCommand createTextCommand;

    @Autowired
    UpdateTextCommand updateTextCommand;

    @Autowired
    DeletePostCommand deletePostCommand;

    @Autowired
    ReblogPostCommand reblogPostCommand;


    //-- REST methods
    @RequestMapping(method = RequestMethod.GET, value="/{bid}/post/{id}")
    @ApiOperation(value="Returns a post", notes = "Return a post by its ID")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found")
    })
    public PostResponse get(
            @PathVariable long bid,
            @PathVariable long id
    ) {
        return getPostService.execute(id, new CommandContextImpl().withBlogId(bid));
    }

    @RequestMapping(method = RequestMethod.GET, value="/{bid}/posts")
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
        return getPostListService.execute(null,
                new CommandContextImpl().withBlogId(bid).withLimit(limit).withOffset(offset)
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, value="/{bid}/post/{id}")
    @ApiOperation(value="Delete a post")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to delete the post.")
    })
    public void delete(
            @RequestHeader(value="access_token", required = false) String accessToken,
            @PathVariable long bid,
            @PathVariable long id
    ) {
        deletePostCommand.execute(
                null,
                new CommandContextImpl().withAccessTokenId(accessToken).withBlogId(bid).withId(id)
        );
    }


    @RequestMapping(method = RequestMethod.POST, value="/{bid}/post/{id}/reblog")
    @ApiOperation(value="Delete a post")
    @ApiResponses({
            @ApiResponse(code=201, message = "Success - Post successfully added"),
            @ApiResponse(code=200, message = "Success - Post was already in blog"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to re-blog the post.")
    })
    public ResponseEntity reblog(
            @RequestHeader(value="access_token", required = false) String accessToken,
            @PathVariable long bid,
            @PathVariable long id
    ) {
        try {
            reblogPostCommand.execute(null, new CommandContextImpl().withAccessTokenId(accessToken).withBlogId(bid).withId(id));
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (DuplicatePostException e){ // NOSONAR
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.POST}, value="/{bid}/text")
    @ApiOperation(value="Create a new Text")
    @ApiResponses({
            @ApiResponse(code=201, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to delete the post."),
            @ApiResponse(code=404, message = "Bad request data.")
    })
    public ResponseEntity<PostResponse> createText(
            @RequestHeader(value="access_token", required = false) String accessToken,
            @PathVariable long bid,
            @Valid @RequestBody CreateTextRequest request
    ) {
        PostResponse response = createTextCommand.execute(
                request,
                new CommandContextImpl().withAccessTokenId(accessToken).withBlogId(bid)
        );
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value="/{bid}/text/{id}")
    @ApiOperation(value="Update a Text")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to update the post."),
            @ApiResponse(code=404, message = "Invalid request data.")
    })
    public PostResponse updateText(
            @RequestHeader(value="access_token", required = false) String accessToken,
            @PathVariable long bid,
            @PathVariable long id,
            @RequestBody @Valid UpdateTextRequest request
    ) {
        return updateTextCommand.execute(
                request,
                new CommandContextImpl().withAccessTokenId(accessToken).withBlogId(bid).withId(id)
        );
    }


    //-- Exception Handler
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ErrorResponse notFound() {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "not_found");
    }

    @ResponseStatus(value= HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessTokenException.class)
    public ErrorResponse authenticationFailed(AccessTokenException exception) {
        LOG.error("Authentication error", exception);
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "auth_failed", exception.getMessage());
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationException.class)
    public ErrorResponse authorizationFailed(Exception exception) {
        LOG.error("Authorization failed", exception);
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse validationFailed(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), fieldErrors.get(0).getDefaultMessage());
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse failure(Exception exception) {
        LOG.error("Unexpected error", exception);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }
}
