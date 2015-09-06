package com.tchepannou.blog.service;

import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.client.v1.SearchRequest;

public interface SearchCommand extends Command<SearchRequest, PostCollectionResponse> {
}
