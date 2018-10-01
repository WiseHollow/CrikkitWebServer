package com.crikkit.webserver.responses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class HttpHeader {

    public static class HttpHeaderBuilder {

        private static DateTimeFormatter standardFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        private static DateFormat timezoneFormat = new SimpleDateFormat(" z");
        private String protocol, date, contentType, contents;

        protected HttpHeaderBuilder() {
            String timezone = timezoneFormat.format(Calendar.getInstance().getTime());
            LocalDateTime now = LocalDateTime.now();
            date = "Date: " + standardFormat.format(now) + timezone;
        }

        public HttpHeader build() {
            return new HttpHeader(protocol, date, contentType, contents);
        }

        public String getProtocol() {
            return protocol;
        }

        public HttpHeaderBuilder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public String getDate() {
            return date;
        }

        public HttpHeaderBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public String getContentType() {
            return contentType;
        }

        public HttpHeaderBuilder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public String getContents() {
            return contents;
        }

        public HttpHeaderBuilder setContents(String contents) {
            this.contents = contents;
            return this;
        }
    }

    public static HttpHeaderBuilder create() {
        return new HttpHeaderBuilder();
    }

    private final String protocol, date, contentType, contents;

    private HttpHeader(String protocol, String date, String contentType, String contents) {
        this.protocol = protocol;
        this.date = date;
        this.contentType = contentType;
        this.contents = contents;
    }

    public String[] toArray() {
        String[] headers = new String[] {
                protocol, date, contentType, "\r\n", contents
        };

        return headers;
    }

}
