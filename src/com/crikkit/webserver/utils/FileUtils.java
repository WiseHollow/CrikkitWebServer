package com.crikkit.webserver.utils;

import com.crikkit.webserver.Server;
import com.crikkit.webserver.Settings;
import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.logs.CrikkitLogger;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.nio.file.Files;

public class FileUtils {

    public static boolean deleteFolder(@NotNull File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        return folder.delete();
    }

    public static String fileToString(@NotNull File file) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded);
    }

    public static String requestHttpFileContents(@NotNull File file) {
        if (file.exists()) {
            return fileToString(file);
        } else {
            CrikkitLogger.getInstance().severe("The requested resource was not found: " + file.getPath());
            return Settings.getInstance().getStatus404Html();
        }
    }

    public static String getHttpFileContents(@NotNull File file) throws HttpPageNotFoundException {
        if (file.exists()) {
            return fileToString(file);
        } else {
            CrikkitLogger.getInstance().severe("The requested resource was not found: " + file.getPath());
            throw new HttpPageNotFoundException(file.getPath());
        }
    }

    public static void exportInternalFile(@NotNull String resource, @NotNull File output) throws FileNotFoundException {
        InputStream fis = Server.class.getClassLoader().getResourceAsStream(resource);
        if (fis == null) {
            throw new FileNotFoundException();
        }
        FileOutputStream fos = null;
        try {
            output.createNewFile();
            fos = new FileOutputStream(output);
            byte[] buf = new byte[1024];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            CrikkitLogger.getInstance().info("Successfully copied resource file.");
        } catch (Exception e) {
            CrikkitLogger.getInstance().warning("Failed to load resource file from JAR!");
            CrikkitLogger.getInstance().severe(e);
        } finally {
            try {
                fis.close();
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
