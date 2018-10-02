package com.crikkit.webserver.logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class CrikkitLogger {

    private static CrikkitLogger instance;

    public static CrikkitLogger getInstance() {
        if (instance == null) {
            try {
                instance = new CrikkitLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    private Logger logger;

    protected CrikkitLogger() throws IOException {
        logger = Logger.getLogger("");
        File logDirectory = new File("logs");
        if (!logDirectory.isDirectory()) {
            boolean result = logDirectory.mkdirs();
            if (!result)
                throw new IOException("Could not create log directory. Access violation?");
        }
        File infoLog = new File("logs" + File.separator + "info.log");
        File exceptionsLog = new File("logs" + File.separator + "exceptions.log");
        if (!infoLog.isFile()) {
            boolean result = infoLog.createNewFile();
            if (!result)
                throw new IOException("Could not create info log file. Access violation?");
        }
        if (!exceptionsLog.isFile()) {
            boolean result = exceptionsLog.createNewFile();
            if (!result)
                throw new IOException("Could not create exception log file. Access violation?");
        }

        logger.addHandler(new CrikkitLogHandler(
                new PrintWriter(new FileWriter(infoLog, true)),
                new PrintWriter(new FileWriter(exceptionsLog, true)))
        );
    }

    public void severe(String msg) {
        logger.severe(msg);
    }

    public void severe(Exception exception) {
        logger.severe(exception.getMessage());
        Arrays.stream(exception.getStackTrace()).forEach(element -> logger.severe(element.toString()));
    }

    public void warning(String msg) {
        logger.warning(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(Object msg) {
        logger.info(msg.toString());
    }

    public void close() {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }
}
