package com.crikkit.webserver.handlers;

import com.crikkit.webserver.responses.HttpStatus;
import com.crikkit.webserver.utils.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Stream;

public class HttpResponse {

    private HttpRequest httpRequest;
    private PrintWriter writer;

    public HttpResponse(PrintWriter writer, HttpRequest httpRequest) {
        this.writer = writer;
        this.httpRequest = httpRequest;
    }

    private String getHTTPFileContents(File file) {
        if (file.exists()) {
            return FileUtils.fileToString(file);
        } else {
            System.err.println("The requested resource was not found: " + file.getPath());
            return "<html><body><h1>That page was not found!</h1></body></html>";
        }
    }

    private Stream<String> createHeaders(HttpStatus httpStatus) {
        String[] headers = new String[] {
                "HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getPhrase(),
                "Content-type: text/html; charset=UTF-8",
                "Date: Sun, 20 Oct 2018 18:39:30 GMT"
        };
        return Arrays.stream(headers);
    }

    public void send() {
        //TODO: Patch possible exploit with "../" and other things.
        File requestedFile = new File("public_html" + httpRequest.getPath());
        HttpStatus httpStatus = requestedFile.exists() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        createHeaders(httpStatus).forEach(header -> writer.println(header));
        writer.println("\r\n");
        writer.print(getHTTPFileContents(requestedFile));

        writer.flush();
        writer.close();
    }

}
