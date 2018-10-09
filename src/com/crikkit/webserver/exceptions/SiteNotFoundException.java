package com.crikkit.webserver.exceptions;

public class SiteNotFoundException extends RuntimeException {

    public SiteNotFoundException(String site) {
        super("Could not locate site: " + site);
    }

}
