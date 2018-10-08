package com.crikkit.webserver.responses;

import com.crikkit.webserver.CSEM;
import com.crikkit.webserver.Settings;
import com.crikkit.webserver.exceptions.HttpResponseBuildException;
import com.crikkit.webserver.exceptions.SiteNotFoundException;
import com.crikkit.webserver.requests.HttpRequest;
import com.crikkit.webserver.sites.Site;
import com.crikkit.webserver.sites.WebPage;
import com.crikkit.webserver.utils.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

public class HttpResponse {

    public static class Builder {
        private HttpRequest httpRequest;
        private PrintWriter writer;

        public Builder() { }

        public Builder setRequest(HttpRequest httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }

        public Builder setWriter(PrintWriter writer) {
            this.writer = writer;
            return this;
        }

        public HttpRequest getHttpRequest() {
            return httpRequest;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public HttpResponse create() {
            if (httpRequest == null || writer == null) {
                throw new HttpResponseBuildException();
            }
            //TODO: Patch possible exploit with "../" and other things.
            String path = httpRequest.getPath().split("\\?")[0];
            //TODO: Pretty-up path elsewhere?
            if (path.equals("/")) {
                path = "/index.html";
            }
            if (Settings.getInstance().isRequireExtensions() && !path.endsWith("/") && !path.contains(".")) {
                path += "." + Settings.getInstance().getExpectedExtension();
            }

            Optional<Site> optionalRequestedSite = Site.getSite(httpRequest.getHost());
            if (!optionalRequestedSite.isPresent()) {
                throw new SiteNotFoundException(httpRequest.getHost());
            }

            Site requestedSite = optionalRequestedSite.get();
            WebPage webPage = requestedSite.getWebPageHtml(path);
            return new HttpResponse(writer, webPage);
        }
    }

    private PrintWriter writer;
    private HttpStatus httpStatus;
    private String html;

    public HttpResponse(PrintWriter writer, WebPage webPage) {
        this.writer = writer;
        this.httpStatus = webPage.getHttpStatus();
        this.html = webPage.getHtml();
    }

    public void send() {
        HttpHeader.HttpHeaderBuilder httpHeaderBuilder = HttpHeader.create()
                .setProtocol("HTTP/1.1 " + httpStatus.getStatusCode() + " " + httpStatus.getPhrase())
                .setContentType("Content-type: text/html; charset=UTF-8")
                .setContents(html);
        Arrays.stream(httpHeaderBuilder.build().toArray()).forEach(header -> writer.println(header));

        writer.flush();
        writer.close();
    }

}
