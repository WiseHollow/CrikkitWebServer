package com.crikkit.webserver.utils;

import com.crikkit.webserver.Settings;
import com.crikkit.webserver.exceptions.HttpPageNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static String fileToString(File file) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded);
    }

    public static String requestHttpFileContents(File file) {
        if (file.exists()) {
            return fileToString(file);
        } else {
            System.err.println("The requested resource was not found: " + file.getPath());
            return Settings.getInstance().getStatus404Html();
        }
    }

    public static String getHttpFileContents(File file) throws HttpPageNotFoundException {
        if (file.exists()) {
            return fileToString(file);
        } else {
            System.err.println("The requested resource was not found: " + file.getPath());
            throw new HttpPageNotFoundException(file.getPath());
        }
    }

}
