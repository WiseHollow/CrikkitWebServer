package com.crikkit.webserver.commands;

import com.crikkit.webserver.commands.listeners.CommandListener;
import com.crikkit.webserver.logs.CrikkitLogger;

public class CommandHelp implements CommandExecutor {

    private CrikkitLogger logger;

    public CommandHelp() {
        logger = CrikkitLogger.getInstance();
    }

    @Override
    public void execute(String command, String[] args) {
        CommandListener.getCommandList().forEach(commandExecutor -> logger.info(commandExecutor.getSyntax()));
    }

    @Override
    public String getSyntax() {
        return "help";
    }
}
