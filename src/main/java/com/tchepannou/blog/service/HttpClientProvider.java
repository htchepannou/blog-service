package com.tchepannou.blog.service;

import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpClientProvider {
    CloseableHttpClient getHttpClient ();
}
