package com.crikkit.webserver.exceptions;

public class HttpPageNotFoundException extends Exception {

    public HttpPageNotFoundException(String path) {
        super("The file could not be found: " + path);
    }

}
