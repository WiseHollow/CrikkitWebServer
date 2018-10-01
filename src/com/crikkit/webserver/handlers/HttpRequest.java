package com.crikkit.webserver.handlers;

import com.crikkit.webserver.Settings;
import com.crikkit.webserver.exceptions.HttpRequestException;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    public enum RequestType { GET }

    private BufferedReader reader;

    private String host, path, userAgent, protocol;
    private RequestType type;

    public HttpRequest(BufferedReader reader) {
        this.reader = reader;
        try {
            parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    private void parse(BufferedReader reader) throws IOException {
        String requestHeader = reader.readLine();
        if (requestHeader != null) {
            String[] requestHeaderElements = requestHeader.split(" ");
            if (requestHeaderElements.length == 3) {
                try {
                    type = RequestType.valueOf(requestHeaderElements[0]);
                } catch (Exception exception) {
                    throw new HttpRequestException(requestHeaderElements[0]);
                }

                //TODO: Perhaps put this somewhere else.
                path = requestHeaderElements[1];
                if (path.equals("/")) {
                    path = "/index.html";
                }

                if (!path.contains(".")) {
                    path += "." + Settings.getInstance().getExpectedExtension();
                }

                protocol = requestHeaderElements[2];
            }
        } else {
            throw new HttpRequestException(requestHeader);
        }

        //TODO: Store the rest of the request data from reader.
    }

    @Override
    public String toString() {
        return type.name() + " " + path + " " + protocol;
    }

}
