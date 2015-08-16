package com.tchepannou.blog.rr;

public class ErrorResponse {
    private int code;
    private String text;
    private String detailText;

    public ErrorResponse(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public ErrorResponse(int code, String text, String detailText) {
        this.code = code;
        this.text = text;
        this.detailText = detailText;
    }

    public String getDetailText() {
        return detailText;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
