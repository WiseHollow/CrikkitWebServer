package com.crikkit.webserver.handlers;

import com.crikkit.webserver.requests.HttpRequest;
import com.crikkit.webserver.responses.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private Socket socket;

    private PrintWriter writer;
    private BufferedReader reader;

    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        HttpRequest httpRequest = new HttpRequest(reader);
        System.out.println(httpRequest);
        HttpResponse httpResponse = new HttpResponse(writer, httpRequest);
        httpResponse.send();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
