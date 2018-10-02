package com.crikkit.webserver.exceptions;

import com.crikkit.webserver.sites.Site;

public class SiteAlreadyInitializedException extends Exception {

    public SiteAlreadyInitializedException(Site site) {
        super("The given site host has already been initialized: " + site.getHost());
    }

}
