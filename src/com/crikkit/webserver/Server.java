package com.crikkit.webserver;

import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.handlers.ConnectionHandler;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server extends Thread {

    private static Server instance;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }

        return instance;
    }

    private boolean active;
    private ServerSocket serverSocket;

    private Server() { }

    boolean isActive() {
        return active;
    }

    void initialize() throws IOException, HttpPageNotFoundException {
        if (!active) {
            Settings settings = Settings.getInstance();
            generateDirectories();
            settings.load();
            CrikkitLogger.getInstance().info("Starting server on port: " + settings.getPort());
            serverSocket = new ServerSocket(settings.getPort());
            active = true;
        }
    }

    void generateDirectories() {
        CrikkitLogger.getInstance().info("Generating required directories..");
        File publicHtml = new File("public_html");
        if (!publicHtml.isDirectory()) {
            boolean result = publicHtml.mkdirs();
            if (!result) {
                CrikkitLogger.getInstance().severe("Failed to create public_html directory.");
            }
        }
    }

    void listen() throws IOException {
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
        } catch (SocketException e) {
            if (active) {
                CrikkitLogger.getInstance().severe(e);
            }
            return;
        }
        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
        connectionHandler.start();
    }

    public void close() throws IOException {
        active = false;
        serverSocket.close();
        CrikkitLogger.getInstance().close();
    }

    @Override
    public void run() {
        while (active) {
            try {
                listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
