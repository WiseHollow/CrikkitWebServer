package com.crikkit.webserver.commands;

public interface CommandExecutor {

    void execute(String command, String[] args);

    String getSyntax();

}
