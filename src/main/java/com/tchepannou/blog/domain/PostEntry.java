package com.tchepannou.blog.domain;

import java.util.Date;

public class PostEntry extends Model {
    private long postId;
    private long blogId;
    private Date posted;

    public PostEntry() {
    }

    public PostEntry(long postId, long blogId) {
        this.postId = postId;
        this.blogId = blogId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date created) {
        this.posted = created;
    }
}
