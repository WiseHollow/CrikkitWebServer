package com.crikkit.webserver.commands;

import com.crikkit.webserver.Server;
import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;

public class CommandStatus implements CommandExecutor {

    private CrikkitLogger logger;

    public CommandStatus() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        Site.getSites().forEach(site -> logger.info(site));
        Server.getInstance().logMemoryStats();
    }

    @Override
    public String getSyntax() {
        return "status";
    }
}
