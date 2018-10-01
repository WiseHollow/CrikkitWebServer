package com.crikkit.webserver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        try {
            server.initialize();
        } catch (IOException e) {
            System.out.println("Failed to initialize Crikkit. Exiting..");
            e.printStackTrace();
            return;
        }

        while (server.isActive()) {
            try {
                server.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("Closing Crikkit Web Server..");
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
