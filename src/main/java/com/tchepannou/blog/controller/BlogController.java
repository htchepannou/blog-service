package com.tchepannou.blog.controller;

import com.tchepannou.blog.client.v1.CreatePostRequest;
import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.client.v1.SearchRequest;
import com.tchepannou.blog.client.v1.UpdatePostRequest;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.service.command.CreateCommand;
import com.tchepannou.blog.service.command.DeleteCommand;
import com.tchepannou.blog.service.command.GetCommand;
import com.tchepannou.blog.service.command.ReblogCommand;
import com.tchepannou.blog.service.command.SearchCommand;
import com.tchepannou.blog.service.command.UpdateCommand;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Api(basePath = "/v1/blog", value = "Blog API", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value="/v1/blog", produces = MediaType.APPLICATION_JSON_VALUE)
public class BlogController {

    //-- Attributes
    private static final Logger LOG = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    GetCommand getPostService;

    @Autowired
    CreateCommand createTextCommand;

    @Autowired
    UpdateCommand updateTextCommand;

    @Autowired
    DeleteCommand deletePostCommand;

    @Autowired
    ReblogCommand reblogPostCommand;

    @Autowired
    SearchCommand searchCommand;


    //-- REST methods
    @RequestMapping(method = RequestMethod.GET, value="/{bid}/posts")
    @ApiOperation(value="Return all the posts")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success")
    })
    public PostCollectionResponse all(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable(value = "bid") String bid,
            @RequestParam (value = "limit", defaultValue = "20") int limit,
            @RequestParam (value = "offset", defaultValue = "0") int offset
    ) {
        return all(transactionId, bid, SearchRequest.DEFAULT_STATUS, limit, offset);
    }

    @RequestMapping(method = RequestMethod.GET, value="/{bid}/posts/published")
    @ApiOperation(value="Return all the published posts")
    @ApiResponses({
            @ApiResponse(code=200, message = "Success")
    })
    public PostCollectionResponse published(
            @RequestHeader(Http.HEADER_TRANSACTION_ID) String transactionId,
            @PathVariable(value = "bid") String bid,
            @RequestParam (value = "limit", defaultValue = "20") int limit,
            @RequestParam (value = "offset", defaultValue = "0") int offset
    ) {
        return all(transactionId, bid, Post.Status.published.name(), limit, offset);
    }

    private PostCollectionResponse all (
            final String transactionId,
            final String id,
            final String status,
            final int limit,
            final int offset
    ){
        SearchRequest request = new SearchRequest();
        request.setBlogIds(toLongList(id));
        request.setStatus(status);

        return searchCommand.execute(
                request,
                new CommandContextImpl()
                        .withTransactionId(transactionId)
                        .withLimit(limit)
                        .withOffset(offset)
        );
    }

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
            @ApiResponse(code=201, message = "Success - Post successfully added", response = ResponseEntity.class),
            @ApiResponse(code=200, message = "Success - Post was already in blog", response = ResponseEntity.class),
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
            @ApiResponse(code=200, message = "Success"),
            @ApiResponse(code=404, message = "Bad request.")
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
            @ApiResponse(code=400, message = "Invalid request.")
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


    //-- Private

    private Set<Long> toLongList (String id){
        return Arrays.asList(id.split("\\+"))
                .stream()
                .map( i -> toLong(i) )
                .filter(i -> i > 0)
                .collect(Collectors.toSet());
    }

    private Long toLong (String str){
        try{
            return Long.parseLong(str.trim());
        } catch (Exception e){  // NOSONAR
            return null;
        }
    }

    //-- Exception Handler
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ErrorResponse notFoundError(final HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND.value(), "not_found", request);
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
