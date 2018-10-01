package com.crikkit.webserver;

import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.handlers.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private boolean active;
    private ServerSocket serverSocket;

    public Server() { }

    boolean isActive() {
        return active;
    }

    void initialize() throws IOException, HttpPageNotFoundException {
        if (!active) {
            System.out.println("Loading configuration file..");
            Settings settings = Settings.getInstance();
            settings.loadFromConfiguration();
            System.out.println("Starting server on port: " + settings.getPort());
            serverSocket = new ServerSocket(settings.getPort());
            active = true;
        }
    }

    void listen() throws IOException {
        Socket clientSocket = serverSocket.accept();
        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
        connectionHandler.start();
    }

    void close() throws IOException {
        serverSocket.close();
        active = false;
    }

}
