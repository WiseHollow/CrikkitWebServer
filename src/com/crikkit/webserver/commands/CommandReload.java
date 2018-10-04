package com.crikkit.webserver.commands;

import com.crikkit.webserver.Server;
import com.crikkit.webserver.Settings;
import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.logs.CrikkitLogger;

public class CommandReload implements CommandExecutor {

    private CrikkitLogger logger;

    public CommandReload() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        try {
            Settings.getInstance().loadConfig();
        } catch (HttpPageNotFoundException e) {
            logger.severe(e);
        }
        Server.getInstance().reload();
        logger.info("Finished reloading server.");
    }

    @Override
    public String getSyntax() {
        return "help";
    }
}
