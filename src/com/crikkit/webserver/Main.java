package com.crikkit.webserver;

import com.crikkit.webserver.commands.listeners.CommandListener;
import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.logs.CrikkitLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        Server server = Server.getInstance();
        CrikkitLogger crikkitLogger = CrikkitLogger.getInstance();

        try {
            server.initialize();
        } catch (IOException exception) {
            crikkitLogger.severe("Failed to bind to port " + Settings.getInstance().getPort() + ". In use?");
            crikkitLogger.severe(exception);
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
    }

}
