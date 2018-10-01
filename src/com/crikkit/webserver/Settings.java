package com.crikkit.webserver;

import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
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

    private String version;
    private int port;
    private String expectedExtension;
    private boolean requireExtensions;

    private boolean status404RedirectDefaultPath;
    private String  status404RedirectPath;
    private String  status404Html;

    private void copyConfiguration() {
        File config = new File("config.json");
        exportInternalFile("config.json", config);
    }

    public void loadFromConfiguration() throws HttpPageNotFoundException {
        File config = new File("config.json");
        if (!config.exists()) {
            copyConfiguration();
        }

        JSONObject configurationObject = new JSONObject(FileUtils.fileToString(config));
        version = configurationObject.getString("version");
        port = configurationObject.getInt("bind-to-port");

        JSONObject extensionSettings = configurationObject.getJSONArray("extensions").getJSONObject(0);
        expectedExtension = extensionSettings.getString("expected-extension");
        requireExtensions = extensionSettings.getBoolean("require-extensions");

        JSONObject status404JSONObject = configurationObject.getJSONArray("status-handling")
                .getJSONObject(0).getJSONArray("404-not-found").getJSONObject(0);
        status404RedirectDefaultPath = status404JSONObject.getBoolean("overwrite-default-redirect");
        status404RedirectPath = status404JSONObject.getString("redirect-path");
        if (status404RedirectDefaultPath) {
            status404Html = FileUtils.getHttpFileContents(new File(status404RedirectPath));
        } else {
            status404Html = "<html><body><h1>That page was not found!</h1></body></html>";
        }
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

    public String getVersion() {
        return version;
    }

    public String getStatus404Html() {
        return status404Html;
    }
}
