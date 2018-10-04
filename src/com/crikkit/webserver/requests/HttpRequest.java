package com.crikkit.webserver.requests;

import com.crikkit.webserver.exceptions.HttpRequestException;
import com.crikkit.webserver.exceptions.HttpUnhandledRequestType;
import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Stream;

public class HttpRequest {

    public enum RequestType { GET }

    private String host, path, userAgent, protocol;
    private RequestType type;

    public HttpRequest(BufferedReader reader) {
        try {
            parse(reader);
        } catch (IOException | HttpRequestException | HttpUnhandledRequestType e) {
            CrikkitLogger.getInstance().severe(e);
        }
    }

    public String getPath() {
        return path;
    }

    public String getHost() {
        return host;
    }

    private void parse(BufferedReader reader) throws IOException, HttpUnhandledRequestType, HttpRequestException {
        String requestHeader = reader.readLine();
        if (requestHeader != null) {
            String[] requestHeaderElements = requestHeader.split(" ");
            if (requestHeaderElements.length == 3) {
                try {
                    type = RequestType.valueOf(requestHeaderElements[0]);
                } catch (Exception exception) {
                    throw new HttpUnhandledRequestType(requestHeaderElements[0]);
                }

                path = requestHeaderElements[1];
                protocol = requestHeaderElements[2];
            }
        } else {
            throw new HttpRequestException();
        }

        //TODO: Store the rest of the request data from reader.
        parseHeader(reader.readLine());
    }

    private void parseHeader(String line) {
        String[] elements = line.split(": ");
        switch (elements[0]) {
            case "Host":
                host = elements[1].toLowerCase();
                if (host.startsWith("www."))
                    host = host.substring(4);
                if (host.contains(":"))
                    host = host.substring(0, host.indexOf(":"));
                break;
        }
    }

    @Override
    public String toString() {
        return host + " " + type.name() + " " + path + " " + protocol;
    }

}
