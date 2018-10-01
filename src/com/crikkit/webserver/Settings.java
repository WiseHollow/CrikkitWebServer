package com.crikkit.webserver;

import com.crikkit.webserver.utils.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Settings {

    private static Settings instance;
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    private Settings() { }

    private int port;
    private String expectedExtension;
    private boolean requireExtensions;

    private void copyConfiguration() {
        File config = new File("config.json");
        exportInternalFile("config.json", config);
    }

    public void loadFromConfiguration() {
        File config = new File("config.json");
        if (!config.exists()) {
            copyConfiguration();
        }

        JSONObject configurationObject = new JSONObject(FileUtils.fileToString(config));
        port = configurationObject.getInt("bind-to-port");
        expectedExtension = configurationObject.getString("expected-extension");
        requireExtensions = configurationObject.getBoolean("require-extensions");
    }

    private void exportInternalFile(String resource, File output) {
        InputStream fis = getClass().getResourceAsStream(resource);
        FileOutputStream fos = null;
        try {
            output.createNewFile();
            fos = new FileOutputStream(output);
            byte[] buf = new byte[1024];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            System.out.println("Successfully copied resource file.");
        } catch (Exception e) {
            System.err.println("Failed to load config from JAR!");
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public String getExpectedExtension() {
        return expectedExtension;
    }

    public boolean isRequireExtensions() {
        return requireExtensions;
    }
}
