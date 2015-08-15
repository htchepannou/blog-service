package com.tchepannou.blog.exception;

public class AccessTokenException extends RuntimeException {
    public AccessTokenException(String message) {
        super(message);
    }
}
