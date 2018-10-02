package com.crikkit.webserver.commands;

import com.crikkit.webserver.Settings;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.util.Scanner;

public class CommandListener {

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        processCommand(input);
    }

    private void processCommand(String command) {
        CrikkitLogger logger = CrikkitLogger.getInstance();
        switch (command) {
            case "version":
                logger.info("Crikkit WebServer version -> " + Settings.getInstance().getVersion());
                break;
            default:
                logger.warning("Invalid command entered.");
                break;
        }
    }

}
