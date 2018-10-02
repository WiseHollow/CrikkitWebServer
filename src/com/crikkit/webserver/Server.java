package com.crikkit.webserver;

import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.handlers.ConnectionHandler;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private boolean active;
    private ServerSocket serverSocket;

    public Server() { }

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
        Socket clientSocket = serverSocket.accept();
        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
        connectionHandler.start();
    }

    void close() throws IOException {
        serverSocket.close();
        CrikkitLogger.getInstance().close();
        active = false;
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
