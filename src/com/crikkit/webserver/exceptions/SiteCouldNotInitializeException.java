package com.crikkit.webserver.exceptions;

import com.crikkit.webserver.sites.Site;

public class SiteCouldNotInitializeException extends Exception {

    public SiteCouldNotInitializeException(Site site) {
        super("Site could not be initialized: " + site);
    }

}
