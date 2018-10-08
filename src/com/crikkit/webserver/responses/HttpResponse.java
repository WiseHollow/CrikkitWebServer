package com.crikkit.webserver.responses;

import com.crikkit.webserver.CSEM;
import com.crikkit.webserver.Settings;
import com.crikkit.webserver.requests.HttpRequest;
import com.crikkit.webserver.sites.Site;
import com.crikkit.webserver.utils.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

public class HttpResponse {

    private HttpRequest httpRequest;
    private PrintWriter writer;

    public HttpResponse(PrintWriter writer, HttpRequest httpRequest) {
        this.writer = writer;
        this.httpRequest = httpRequest;
    }

    public void send() {
        //TODO: Patch possible exploit with "../" and other things.
        String path = httpRequest.getPath().split("\\?")[0];
        //TODO: Pretty-up path elsewhere?
        if (path.equals("/")) {
            path = "/index.html";
        }
        if (Settings.getInstance().isRequireExtensions() && !path.endsWith("/") && !path.contains(".")) {
            path += "." + Settings.getInstance().getExpectedExtension();
        }

        HttpStatus httpStatus;
        File requestedFile;
        String html;

        Optional<Site> optionalRequestedSite = Site.getSite(httpRequest.getHost());
        if (optionalRequestedSite.isPresent()) {
            Site requestedSite = optionalRequestedSite.get();
            if (requestedSite.isEnabled()) {
                requestedFile = new File(optionalRequestedSite.get().getSitePublicHtmlDirectory() + path);
                httpStatus = requestedFile.exists() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
                html = FileUtils.requestHttpFileContents(requestedFile);
            } else {
                html = Settings.getInstance().getStatus503Html();
                httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
        } else {
            return;
        }

        html = CSEM.getInstance().translate(html);

        HttpHeader.HttpHeaderBuilder httpHeaderBuilder = HttpHeader.create()
                .setProtocol("HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getPhrase())
                .setContentType("Content-type: text/html; charset=UTF-8")
                .setContents(html);
        Arrays.stream(httpHeaderBuilder.build().toArray()).forEach(header -> writer.println(header));

        writer.flush();
        writer.close();
    }

}
