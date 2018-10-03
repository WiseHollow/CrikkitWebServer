package com.crikkit.webserver.logs;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CrikkitLogHandler extends Handler {

    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_BLACK = "\u001B[30m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_YELLOW = "\u001B[33m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_PURPLE = "\u001B[35m";
    private final String ANSI_CYAN = "\u001B[36m";
    private final String ANSI_WHITE = "\u001B[37m";

    private PrintWriter infoWriter, exceptionWriter;

    public CrikkitLogHandler(PrintWriter infoWriter, PrintWriter exceptionWriter) {
        this.infoWriter = infoWriter;
        this.exceptionWriter = exceptionWriter;
    }

    @Override
    public void publish(LogRecord record) {
        StringBuilder messageBuilder = new StringBuilder()
                .append(new Timestamp(record.getMillis()))
                .append(" - ")
                .append(record.getLevel().getName())
                .append(" Thread ")
                .append(record.getThreadID())
                .append(": ")
                .append(record.getMessage());
        String message = messageBuilder.toString();
        if (record.getLevel() == Level.SEVERE) {
            System.out.println(ANSI_RED + message);
            severe(message);
        } else {
            System.out.println(ANSI_CYAN + message);
            info(message);
        }

    }

    private void info(String message) {
        infoWriter.println(message);
        infoWriter.flush();
    }

    private void severe(String message) {
        exceptionWriter.println(message);
        exceptionWriter.flush();
    }

    @Override
    public void flush() {
        infoWriter.flush();
        exceptionWriter.flush();
    }

    @Override
    public void close() throws SecurityException {
        infoWriter.flush();
        infoWriter.close();
        exceptionWriter.flush();
        exceptionWriter.close();
    }
}
