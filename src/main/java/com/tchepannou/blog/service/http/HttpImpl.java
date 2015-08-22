package com.tchepannou.blog.service.http;

import com.tchepannou.blog.exception.AccessTokenException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpImpl implements Http{
    //-- Attributes
    @Autowired
    private Jackson2ObjectMapperBuilder jsonBuilder;

    //-- Http overrides
    @Override
    public <T> T get(URL url, Class<T> type) throws IOException{
        try {
            HttpGet request = new HttpGet(url.toURI());

            try (CloseableHttpClient client = HttpClients.createDefault()) {

                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new AccessTokenException("Failure - " + response.getStatusLine());
                }

                return jsonBuilder
                        .build()
                        .readValue(response.getEntity().getContent(), type);

            }
        } catch (URISyntaxException e){
            throw new IllegalStateException("Invalid URL " + url, e);
        }
    }
}
