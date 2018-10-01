package com.crikkit.webserver.responses;

import com.crikkit.webserver.requests.HttpRequest;
import com.crikkit.webserver.utils.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

public class HttpResponse {

    private HttpRequest httpRequest;
    private PrintWriter writer;

    public HttpResponse(PrintWriter writer, HttpRequest httpRequest) {
        this.writer = writer;
        this.httpRequest = httpRequest;
    }

    public void send() {
        //TODO: Patch possible exploit with "../" and other things.
        File requestedFile = new File("public_html" + httpRequest.getPath());
        HttpStatus httpStatus = requestedFile.exists() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        String html = FileUtils.requestHttpFileContents(requestedFile);

        HttpHeader.HttpHeaderBuilder httpHeaderBuilder = HttpHeader.create()
                .setProtocol("HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getPhrase())
                .setDate("Date: Sun, 20 Oct 2018 18:39:30 GMT")
                .setContentType("Content-type: text/html; charset=UTF-8")
                .setContents(html);
        Arrays.stream(httpHeaderBuilder.build().toArray()).forEach(header -> writer.println(header));

        writer.flush();
        writer.close();
    }

}
