package com.tchepannou.blog.domain;

import java.util.Arrays;
import java.util.Date;

public class Post {
    //-- Enums
    public enum Type {
        text(1);

        int val;
        Type (int value){
            this.val = value;
        }
        public int value(){
            return val;
        }

        public static Type fromValue (final int value){
            return Arrays.asList(Type.values()).stream()
                    .filter(type -> type.value() == value)
                    .findFirst()
                    .orElse(null);
        }
    }

    public enum Status {
        draft(0), published(1);

        int val;
        Status (int value){
            this.val = value;
        }
        public int value(){
            return val;
        }

        public static Status fromValue (final int value){
            return Arrays.asList(Status.values()).stream()
                    .filter(status -> status.value() == value)
                    .findFirst()
                    .orElse(null);
        }
    }

    //-- Attributes
    private long id;
    private long blogId;
    private String title;
    private String slug;
    private String content;
    private Post.Type type;
    private Post.Status status;
    private Date published;
    private Date created;
    private Date updated;
    private boolean deleted;


    //-- Getter/Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
