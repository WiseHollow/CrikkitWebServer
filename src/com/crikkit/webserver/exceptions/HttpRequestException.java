package com.crikkit.webserver.exceptions;

public class HttpRequestException extends Exception {

    public HttpRequestException() {
        super("There was an problem with the given request string.");
    }

    public HttpRequestException(String request) {
        super("There was an problem with the given request string: " + request);
    }

}
