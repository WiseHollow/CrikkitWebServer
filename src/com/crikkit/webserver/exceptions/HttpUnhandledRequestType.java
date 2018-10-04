package com.crikkit.webserver.exceptions;

public class HttpUnhandledRequestType extends Exception {

    public HttpUnhandledRequestType(String typeName) {
        super("Received an unhandled request type: " + typeName);
    }

}
