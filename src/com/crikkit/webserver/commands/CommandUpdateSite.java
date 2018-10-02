package com.crikkit.webserver.commands;

import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;
import com.crikkit.webserver.utils.SitesDeclaredUtils;

import java.io.IOException;
import java.util.Optional;

public class CommandUpdateSite implements CommandExecutor {

    public static final String syntax = "updatesite [host] optionals(--enable || --disable)";

    private CrikkitLogger logger;

    public CommandUpdateSite() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        if (args.length <= 1) {
            logger.warning("Invalid command syntax. ex: '" + syntax + "'");
        } else {
            String host = args[0];
            Boolean enabled = null;
            if (args[1].equalsIgnoreCase("--enable")) {
                enabled = true;
            } else if (args[1].equalsIgnoreCase("--disable")) {
                enabled = false;
            }

            Optional<Site> optionalSite = Site.getSite(host);
            if (optionalSite.isPresent()) {
                Site site = optionalSite.get();
                if (enabled != null) {
                    site.setEnabled(enabled);
                }

                try {
                    SitesDeclaredUtils.updateSite(site);
                } catch (IOException e) {
                    logger.severe("Could not update site: " + site);
                    logger.severe(e);
                    return;
                }

                logger.info("Successfully updated site: " + site);
            } else {
                logger.warning("No such site with host: " + args[0]);
            }
        }
    }

    @Override
    public String getSyntax() {
        return syntax;
    }
}
