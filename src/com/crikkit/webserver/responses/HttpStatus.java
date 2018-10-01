package com.crikkit.webserver.responses;

public enum HttpStatus {
    OK(200, "OK"), NOT_FOUND(404, "Not Found");

    private int statusCode;
    private String phrase;

    HttpStatus(int statusCode, String phrase) {
        this.statusCode = statusCode;
        this.phrase = phrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getPhrase() {
        return phrase;
    }
}