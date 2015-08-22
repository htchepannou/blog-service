package com.tchepannou.blog.service.http;

import java.io.IOException;
import java.net.URL;

public interface Http {
    <T> T get(URL url, Class<T> type) throws IOException;
}
