package com.tchepannou.blog.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tchepannou.blog.domain.AccessToken;
import com.tchepannou.blog.exception.AccessTokenException;
import com.tchepannou.blog.service.AccessTokenService;
import com.tchepannou.blog.service.HttpClientProvider;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

public class AccessTokenServiceImpl implements AccessTokenService{
    @Value("${auth.access_token.url}")
    private String url;

    @Autowired
    private HttpClientProvider http;

    @Autowired
    private Jackson2ObjectMapperBuilder jsonBuilder;

    @Override
    public AccessToken get(String accessTokenId) throws AccessTokenException {

        HttpGet request = new HttpGet(String.format("%s/%s", url, accessTokenId));

        try(CloseableHttpClient client = http.getHttpClient()){

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() != 200){
                throw new AccessTokenException("Failure - " + response.getStatusLine());
            }

            return jsonBuilder
                    .build()
                    .readValue(response.getEntity().getContent(), AccessToken.class);

        } catch (JsonProcessingException e){
            throw new AccessTokenException("Unable to process JSON response", e);
        } catch (IOException e){
            throw new AccessTokenException("Connection Error", e);
        }
    }
}
