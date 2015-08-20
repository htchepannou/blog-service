package com.tchepannou.blog.service.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@Deprecated
public class HttpClientProviderImpl implements HttpClientProvider {
    @Override
    public CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
}
