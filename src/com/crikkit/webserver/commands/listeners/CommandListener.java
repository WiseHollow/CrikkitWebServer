package com.crikkit.webserver.commands.listeners;

import com.crikkit.webserver.commands.CommandExecutor;
import com.crikkit.webserver.commands.CommandStop;
import com.crikkit.webserver.commands.CommandVersion;
import com.crikkit.webserver.logs.CrikkitLogger;

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
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        processCommand(input);
    }

    private void processCommand(String fullCommand) {
        fullCommand = fullCommand.toLowerCase();
        int spaceIndex = fullCommand.contains(" ") ? fullCommand.indexOf(" ") : fullCommand.length();
        String command = fullCommand.substring(0, spaceIndex);
        String[] arguments = fullCommand.substring(spaceIndex).split(" ");

        CrikkitLogger logger = CrikkitLogger.getInstance();
        if (!commands.containsKey(command)) {
            logger.warning("Invalid command entered.");
        } else {
            commands.get(command).execute(command, arguments);
        }
    }

}
