package com.tchepannou.blog.client.v1;

import java.util.Collection;
import java.util.HashSet;

public class SearchRequest {
    public static final String DEFAULT_STATUS = "all";

    private Collection<Long> blogIds = new HashSet<>();
    private String status;

    public Collection<Long> getBlogIds() {
        return blogIds;
    }

    public void addBlogId (long id){
        blogIds.add(id);
    }

    public void setBlogIds(Collection<Long> blogIds) {
        this.blogIds = blogIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
