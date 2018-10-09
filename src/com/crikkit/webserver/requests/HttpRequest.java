package com.crikkit.webserver.requests;

import com.crikkit.webserver.exceptions.HttpRequestException;
import com.crikkit.webserver.exceptions.HttpUnhandledRequestType;
import com.crikkit.webserver.logs.CrikkitLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class HttpRequest {

    public enum RequestType { GET, POST }

    private String host, path, userAgent, protocol;
    private String accept, acceptLanguage, acceptEncoding;
    private String referer;
    private String contentType;
    private String dnt;
    private String connection, upgradeInsecureRequests, cacheControl;

    private int contentLength;

    private RequestType type;
    private HashMap<String, String> postData;

    public HttpRequest(BufferedReader reader) {
        postData = new HashMap<>();
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

    public HashMap<String, String> getPostData() {
        return postData;
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

        parseHeader(reader.readLine());
        for (int i = 0; i < 20; i++) {
            boolean finishedReading = parseNextLine(reader);
            if (finishedReading)
                break;
        }
    }

    private boolean parseNextLine(BufferedReader reader) throws IOException, HttpRequestException {
        String line = reader.readLine();
        if (!line.equals("")) {
            parseHeader(line);
            return false;
        } else {
            char[] buffer = new char[this.contentLength];
            try {
                reader.read(buffer, 0, this.contentLength);
            } catch (IOException exception) {
                CrikkitLogger.getInstance().severe("Tried to read too many bytes from stream.");
                CrikkitLogger.getInstance().severe(exception);
                return true;
            }
            String postData = new String(buffer);
            if (postData.length() > 0) {
                String[] elements = postData.split("&");
                for (String element : elements) {
                    try {
                        int indexToSplitFrom = element.indexOf("=");
                        String key = element.substring(0, indexToSplitFrom);
                        key = URLDecoder.decode(key, "UTF-8");
                        String value = element.substring(indexToSplitFrom + 1);
                        value = URLDecoder.decode(value, "UTF-8");
                        this.postData.put(key, value);
                    } catch (UnsupportedEncodingException
                            | StringIndexOutOfBoundsException exception) {
                        CrikkitLogger.getInstance().severe(exception);
                    }
                }
            }
            return true;
        }
    }

    private void parseHeader(String line) throws HttpRequestException, NumberFormatException {
        String[] elements = line.split(": ");
        if (elements.length < 1) {
            throw new HttpRequestException("Header contained invalid data.");
        } else {
            switch (elements[0]) {
                case "Host":
                    host = elements[1].toLowerCase();
                    if (host.startsWith("www."))
                        host = host.substring(4);
                    if (host.contains(":"))
                        host = host.substring(0, host.indexOf(":"));
                    break;
                case "User-Agent":
                    userAgent = elements[1];
                    break;
                case "Accept":
                    accept = elements[1];
                    break;
                case "Accept-Language":
                    acceptLanguage = elements[1];
                    break;
                case "Accept-Encoding":
                    acceptEncoding = elements[1];
                    break;
                case "Referer":
                    referer = elements[1];
                    break;
                case "Content-Type":
                    contentType = elements[1];
                    break;
                case "Content-Length":
                    contentLength = Integer.parseInt(elements[1]);
                    break;
                case "DNT":
                    dnt = elements[1];
                    break;
                case "Connection":
                    connection = elements[1];
                    break;
                case "Upgrade-Insecure-Requests":
                    upgradeInsecureRequests = elements[1];
                    break;
                case "Cache-Control":
                    cacheControl = elements[1];
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return host + " " + type.name() + " " + path + " " + protocol;
    }

}
