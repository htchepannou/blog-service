package com.tchepannou.blog.controller;

import com.tchepannou.blog.client.v1.CreatePostRequest;
import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.exception.AuthorizationException;
import com.tchepannou.blog.service.CreatePostCommand;
import com.tchepannou.blog.service.DeletePostCommand;
import com.tchepannou.blog.service.GetPostCommand;
import com.tchepannou.blog.service.GetPostListCommand;
import com.tchepannou.blog.service.ReblogPostCommand;
import com.tchepannou.blog.service.UpdatePostCommand;
import com.tchepannou.core.client.v1.ErrorResponse;
import com.tchepannou.core.http.Http;
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

import javax.servlet.http.HttpServletRequest;
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
    CreatePostCommand createTextCommand;

    @Autowired
    UpdatePostCommand updateTextCommand;

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
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable long bid,
            @PathVariable long id
    ) {
        return getPostService.execute(
                id,
                new CommandContextImpl()
                        .withBlogId(bid)
                        .withTransactionId(transactionId)
        );
    }

    @RequestMapping(method = RequestMethod.GET, value="/{bid}/posts")
    @ApiOperation(value="List posts", notes = "Return a post by its ID")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found")
    })
    public PostCollectionResponse list(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable long bid,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value="offset", defaultValue = "0") int offset
    ) {
        return getPostListService.execute(null,
                new CommandContextImpl()
                        .withTransactionId(transactionId)
                        .withBlogId(bid)
                        .withLimit(limit)
                        .withOffset(offset)
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, value="/{bid}/post/{id}")
    @ApiOperation(value="Delete a post")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found")
    })
    public void delete(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable long bid,
            @PathVariable long id
    ) {
        deletePostCommand.execute(
                null,
                new CommandContextImpl()
                        .withTransactionId(transactionId)
                        .withBlogId(bid)
                        .withId(id)
        );
    }


    @RequestMapping(method = RequestMethod.POST, value="/{bid}/post/{id}/reblog")
    @ApiOperation(value="Delete a post")
    @ApiResponses({
            @ApiResponse(code=201, message = "Success - Post successfully added"),
            @ApiResponse(code=200, message = "Success - Post was already in blog"),
            @ApiResponse(code=404, message = "Post not found")
    })
    public ResponseEntity reblog(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable long bid,
            @PathVariable long id
    ) {

        boolean result = reblogPostCommand.execute(null,
                    new CommandContextImpl()
                            .withTransactionId(transactionId)
                            .withBlogId(bid)
                            .withId(id)
            );

        return result ? new ResponseEntity(HttpStatus.CREATED) : new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.POST}, value="/{bid}/post")
    @ApiOperation(value="Create a new Post")
    @ApiResponses({
            @ApiResponse(code=201, message = "Success"),
            @ApiResponse(code=404, message = "Bad request data.")
    })
    public ResponseEntity<PostResponse> create(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable long bid,
            @Valid @RequestBody CreatePostRequest request
    ) {
        PostResponse response = createTextCommand.execute(
                request,
                new CommandContextImpl()
                        .withTransactionId(transactionId)
                        .withUserId(request.getUserId())
                        .withBlogId(bid)
        );
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value="/{bid}/post/{id}")
    @ApiOperation(value="Update a Post")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Post not found"),
            @ApiResponse(code=401, message = "Access token expired or is invalid"),
            @ApiResponse(code=403, message = "User not allowed to update the post."),
            @ApiResponse(code=404, message = "Invalid request data.")
    })
    public PostResponse update(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable long bid,
            @PathVariable long id,
            @RequestBody @Valid UpdatePostRequest request
    ) {
        return updateTextCommand.execute(
                request,
                new CommandContextImpl()
                        .withTransactionId(transactionId)
                        .withUserId(request.getUserId())
                        .withBlogId(bid).withId(id)
        );
    }


    //-- Exception Handler
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ErrorResponse notFoundError(final HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND.value(), "not_found", request);
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthorizationException.class)
    public ErrorResponse authorizationError(Exception exception, final HttpServletRequest request) {
        LOG.error("Authorization failed", exception);
        return createErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage(), request);
    }

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse validationError(MethodArgumentNotValidException ex, final HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return createErrorResponse(HttpStatus.BAD_REQUEST.value(), fieldErrors.get(0).getDefaultMessage(), request);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse internalError(final Exception exception, final HttpServletRequest request) {
        LOG.error("Unexpected error", exception);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), request);
    }

    private ErrorResponse createErrorResponse(int code, String text, HttpServletRequest request){
        return new ErrorResponse()
                .withCode(code)
                .withText(text)
                .withTransactionId(request.getHeader(Http.HEADER_TRANSACTION_ID));
    }
}
