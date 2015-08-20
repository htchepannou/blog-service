package com.tchepannou.blog.service.http;

import org.apache.http.impl.client.CloseableHttpClient;

@Deprecated
public interface HttpClientProvider {
    CloseableHttpClient getHttpClient ();
}
