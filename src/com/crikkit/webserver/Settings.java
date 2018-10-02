package com.crikkit.webserver;

import com.crikkit.webserver.exceptions.HttpPageNotFoundException;
import com.crikkit.webserver.exceptions.SiteAlreadyInitializedException;
import com.crikkit.webserver.exceptions.SiteCouldNotInitializeException;
import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;
import com.crikkit.webserver.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

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

    private boolean status503RedirectDefaultPath;
    private String  status503RedirectPath;
    private String  status503Html;

    private void copyConfiguration() {
        File config = new File("configurations" + File.separator + "config.json");
        try {
            FileUtils.exportInternalFile("configurations/config.json", config);
        } catch (FileNotFoundException e) {
            CrikkitLogger.getInstance().severe(e);
        }
    }

    private void copySitesDeclared() {
        File config = new File("configurations" + File.separator + "sites_declared.json");
        try {
            FileUtils.exportInternalFile("configurations/sites_declared.json", config);
        } catch (FileNotFoundException e) {
            CrikkitLogger.getInstance().severe(e);
        }
    }

    public void loadConfig() throws HttpPageNotFoundException {
        CrikkitLogger.getInstance().info("Loading configurations file..");
        File config = new File("configurations" + File.separator + "config.json");
        if (!config.exists()) {
            copyConfiguration();
        }

        JSONObject configurationObject = new JSONObject(FileUtils.fileToString(config));
        version = configurationObject.getString("version");
        port = configurationObject.getInt("bind-to-port");

        JSONObject extensionSettings = configurationObject.getJSONArray("extensions").getJSONObject(0);
        expectedExtension = extensionSettings.getString("expected-extension");
        requireExtensions = extensionSettings.getBoolean("require-extensions");

        // Redirect rules
        JSONObject status404JSONObject = configurationObject.getJSONArray("status-handling")
                .getJSONObject(0).getJSONArray("404-not-found").getJSONObject(0);
        status404RedirectDefaultPath = status404JSONObject.getBoolean("overwrite-default-redirect");
        status404RedirectPath = status404JSONObject.getString("overwrite-redirect-path");
        if (status404RedirectDefaultPath) {
            status404Html = FileUtils.getHttpFileContents(new File(status404RedirectPath));
        } else {
            status404Html = "<html><body><h1>That page was not found!</h1></body></html>";
        }

        JSONObject status503JSONObject = configurationObject.getJSONArray("status-handling")
                .getJSONObject(0).getJSONArray("503-service-unavailable").getJSONObject(0);
        status503RedirectDefaultPath = status503JSONObject.getBoolean("overwrite-default-redirect");
        status503RedirectPath = status503JSONObject.getString("overwrite-redirect-path");
        if (status503RedirectDefaultPath) {
            status503Html = FileUtils.getHttpFileContents(new File(status503RedirectPath));
        } else {
            status503Html = "<html><body><h1>This service is currently unavailable.</h1></body></html>";
        }
    }

    public void loadSitesDeclared() {
        CrikkitLogger.getInstance().info("Loading declared sites..");
        File config = new File("configurations" + File.separator + "sites_declared.json");
        if (!config.exists()) {
            copySitesDeclared();
        }

        JSONObject sitesDeclaredObject = new JSONObject(FileUtils.fileToString(config));
        JSONArray sitesArray = sitesDeclaredObject.getJSONArray("sites");
        for (int i = 0; i < sitesArray.length(); i++) {
            JSONObject siteObject = sitesArray.getJSONObject(i);
            String host = siteObject.getString("host");
            boolean enabled = siteObject.getBoolean("enabled");

            Site site = new Site(host, enabled);
            try {
                Site.initializeSite(site);
            } catch (SiteAlreadyInitializedException | SiteCouldNotInitializeException e) {
                CrikkitLogger.getInstance().severe(e);
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

    public String getStatus503Html() {
        return status503Html;
    }
}
