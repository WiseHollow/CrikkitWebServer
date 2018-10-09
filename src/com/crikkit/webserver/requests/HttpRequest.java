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

    public RequestType getRequestType() {
        return type;
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
        if (type == RequestType.POST) {

            this.userAgent = reader.readLine().substring(12);
            this.accept = reader.readLine().substring(8);
            this.acceptLanguage = reader.readLine().substring(17);
            this.acceptEncoding = reader.readLine().substring(17);
            this.referer = reader.readLine().substring(9);
            this.contentType = reader.readLine().substring(14);
            this.contentLength = Integer.parseInt(reader.readLine().substring(16));
            this.dnt = reader.readLine().substring(5);
            this.connection = reader.readLine().substring(12);
            this.upgradeInsecureRequests = reader.readLine().substring(27);
            //this.cacheControl = reader.readLine();
            //CrikkitLogger.getInstance().info("Cache-Control----> " + this.cacheControl); //.substring(15);
            //CrikkitLogger.getInstance().info("EXTRA? : " + reader.readLine());
            //reader.readLine(); // blank line in between header and content.

            for (int i = 0; i < 10; i++) {
                if (reader.readLine().equals("")) {
                    break;
                }
            }

            char[] buffer = new char[this.contentLength];
            try {
                reader.read(buffer, 0, this.contentLength);
            } catch (IOException exception) {
                CrikkitLogger.getInstance().severe("Tried to read too many bytes from stream.");
                CrikkitLogger.getInstance().severe(exception);
                return;
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

//            CrikkitLogger.getInstance().warning(userAgent);
//            CrikkitLogger.getInstance().warning(accept);
//            CrikkitLogger.getInstance().warning(acceptLanguage);
//            CrikkitLogger.getInstance().warning(acceptEncoding);
//            CrikkitLogger.getInstance().warning(referer);
//            CrikkitLogger.getInstance().warning(contentType);
//            CrikkitLogger.getInstance().warning(contentLength);
//            CrikkitLogger.getInstance().warning(dnt);
//            CrikkitLogger.getInstance().warning(connection);
//            CrikkitLogger.getInstance().warning(upgradeInsecureRequests);
//            CrikkitLogger.getInstance().warning(cacheControl);
        }
    }

    private void parseHeader(String line) throws HttpRequestException {
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
            }
        }
    }

    @Override
    public String toString() {
        return host + " " + type.name() + " " + path + " " + protocol;
    }

}
