package com.tchepannou.blog.exception;

public class DuplicatePostException extends RuntimeException {
    public DuplicatePostException(Throwable cause) {
        super(cause);
    }
}
