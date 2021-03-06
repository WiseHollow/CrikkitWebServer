package com.crikkit.webserver.commands;

import com.crikkit.webserver.exceptions.SiteAlreadyInitializedException;
import com.crikkit.webserver.exceptions.SiteCouldNotInitializeException;
import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;
import com.crikkit.webserver.utils.SitesDeclaredUtils;

import java.io.IOException;

public class CommandNewSite implements CommandExecutor {

    public static final String syntax = "newsite [host] optionals(--disabled)";

    private CrikkitLogger logger;

    public CommandNewSite() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        if (args.length == 0) {
            logger.warning("Invalid command syntax. ex: '" + syntax + "'");
        } else {
            String host = args[0];
            if (host.contains(":")) {
                logger.warning("You cannot define a port for the host.");
                return;
            }
            boolean enabled = (args.length == 1 || !args[1].equalsIgnoreCase("--disabled"));
            Site site = new Site(host, enabled);
            try {
                Site.initializeSite(site);
            } catch (SiteAlreadyInitializedException | SiteCouldNotInitializeException e) {
                logger.severe("Could not initialize new site: " + site);
                logger.severe("Nothing updated.");
                logger.severe(e);
                return;
            }
            try {
                SitesDeclaredUtils.addSite(site);
            } catch (IOException e) {
                logger.severe("Could not create new site: " + site);
                logger.severe(e);
                return;
            }

            logger.info("Successfully created new site: " + site);
        }
    }

    @Override
    public String getSyntax() {
        return syntax;
    }
}
