package com.crikkit.webserver;

import com.crikkit.webserver.commands.CommandListener;
import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        Server server = new Server();
        CrikkitLogger crikkitLogger = CrikkitLogger.getInstance();

        try {
            server.initialize();
        } catch (IOException exception) {
            crikkitLogger.severe("Failed to bind to port " + Settings.getInstance().getPort() + ". In use?");
            crikkitLogger.severe(exception.getMessage());
            return;
        } catch (HttpPageNotFoundException e) {
            crikkitLogger.severe("Failed to get required web page.");
            e.printStackTrace();
        }

        server.start();

        CommandListener commandListener = new CommandListener();
        while (server.isActive()) {
            commandListener.listen();
        }

//        while (server.isActive()) {
//            try {
//                server.listen();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        try {
//            crikkitLogger.warning("Closing Crikkit Web Server..");
//            server.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
