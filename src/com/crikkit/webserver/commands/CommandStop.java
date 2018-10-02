package com.crikkit.webserver.commands;

import com.crikkit.webserver.Server;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.io.IOException;

public class CommandStop implements CommandExecutor {

    private CrikkitLogger logger;

    public CommandStop() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        logger.info("Attempting to stop Crikkit..");
        try {
            Server.getInstance().close();
        } catch (IOException e) {
            logger.severe(e);
        }
    }
}
