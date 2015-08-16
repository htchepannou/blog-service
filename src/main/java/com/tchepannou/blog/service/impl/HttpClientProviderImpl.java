package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.service.HttpClientProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientProviderImpl implements HttpClientProvider {
    @Override
    public CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
}
