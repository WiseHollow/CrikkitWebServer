package com.crikkit.webserver.commands.listeners;

import com.crikkit.webserver.commands.*;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public class CommandListener {

    private static HashMap<String, CommandExecutor> commands = new HashMap<>();

    public static Collection<CommandExecutor> getCommandList() {
        return commands.values();
    }

    public CommandListener() {
        register();
    }

    private void register() {
        commands.put("help", new CommandHelp());
        commands.put("version", new CommandVersion());
        commands.put("stop", new CommandStop());
        commands.put("newsite", new CommandNewSite());
        commands.put("delsite", new CommandDeleteSite());
        commands.put("updatesite", new CommandUpdateSite());
        commands.put("status", new CommandStatus());
        commands.put("reload", new CommandReload());
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
