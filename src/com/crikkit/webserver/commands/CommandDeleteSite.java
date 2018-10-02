package com.crikkit.webserver.commands;

import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;
import com.crikkit.webserver.utils.SitesDeclaredUtils;

import java.io.IOException;
import java.util.Optional;

public class CommandDeleteSite implements CommandExecutor {

    private CrikkitLogger logger;

    public CommandDeleteSite() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        if (args.length == 0) {
            logger.warning("Invalid command syntax. ex: 'delsite [host] optionals(--hard)'");
        } else {
            boolean hard = (args.length == 2 && args[1].equalsIgnoreCase("--hard"));

            Optional<Site> optionalSite = Site.getSite(args[0]);
            if (optionalSite.isPresent()) {

                try {
                    SitesDeclaredUtils.deleteSite(optionalSite.get());
                } catch (IOException e) {
                    logger.severe("There was an error while trying to delete the site from sites_declared.");
                    logger.severe(e);
                    return;
                }

                Site.removeSite(optionalSite.get());

                if (hard) {
                    SitesDeclaredUtils.deleteSiteFiles(optionalSite.get());
                    logger.info("Successfully deleted site and all site files were deleted.");
                } else {
                    logger.info("Successfully deleted site and no site files were touched.");
                }
            } else {
                logger.warning("No such site with host: " + args[0]);
            }
        }
    }
}
