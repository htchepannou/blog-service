package com.tchepannou.blog.domain;

public class Tag extends Model {
    //-- Attributes
    private long id;
    private String name;


    //-- Getter/Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
