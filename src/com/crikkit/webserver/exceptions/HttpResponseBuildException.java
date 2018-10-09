package com.crikkit.webserver.exceptions;

public class HttpResponseBuildException extends RuntimeException {

    public HttpResponseBuildException() {
        super("Could not build a proper HTTPResponse.");
    }

}
