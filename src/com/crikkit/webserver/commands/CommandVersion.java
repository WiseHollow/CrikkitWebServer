package com.crikkit.webserver.commands;

import com.crikkit.webserver.Settings;
import com.crikkit.webserver.logs.CrikkitLogger;

public class CommandVersion implements CommandExecutor {

    private CrikkitLogger logger;

    public CommandVersion() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        logger.info("Crikkit WebServer version -> " + Settings.getInstance().getVersion());
    }
}
