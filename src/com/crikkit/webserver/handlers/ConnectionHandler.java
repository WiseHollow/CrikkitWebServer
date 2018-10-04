package com.crikkit.webserver.handlers;

import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.requests.HttpRequest;
import com.crikkit.webserver.responses.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private final Socket socket;

    private final PrintWriter writer;
    private final BufferedReader reader;

    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        HttpRequest httpRequest = new HttpRequest(reader);
        CrikkitLogger.getInstance().info(httpRequest + " from " + socket.getInetAddress().getHostAddress());
        HttpResponse httpResponse = new HttpResponse(writer, httpRequest);
        httpResponse.send();
        try {
            socket.close();
        } catch (IOException exception) {
            CrikkitLogger.getInstance().severe(exception);
        }
    }
}
