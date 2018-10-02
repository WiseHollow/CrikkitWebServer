package com.crikkit.webserver.commands.listeners;

import com.crikkit.webserver.commands.*;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class CommandListener {

    private HashMap<String, CommandExecutor> commands;

    public CommandListener() {
        commands = new HashMap<>();
        register();
    }

    private void register() {
        commands.put("version", new CommandVersion());
        commands.put("stop", new CommandStop());
        commands.put("newsite", new CommandNewSite());
        commands.put("delsite", new CommandDeleteSite());
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        processCommand(input);
    }

    private void processCommand(String fullCommand) {
        fullCommand = fullCommand.toLowerCase();
        boolean argumentsExist = false;
        int spaceIndex;
        if (fullCommand.contains(" ")) {
            spaceIndex = fullCommand.indexOf(" ");
            argumentsExist = true;
        } else {
            spaceIndex = fullCommand.length();
        }
        String command = fullCommand.substring(0, spaceIndex);
        String[] arguments = argumentsExist ? fullCommand.substring(spaceIndex + 1).split(" ") : new String[0];

        CrikkitLogger logger = CrikkitLogger.getInstance();
        if (!commands.containsKey(command)) {
            logger.warning("Invalid command entered.");
        } else {
            commands.get(command).execute(command, arguments);
        }
    }

}
