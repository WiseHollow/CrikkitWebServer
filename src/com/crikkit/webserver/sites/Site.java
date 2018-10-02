package com.crikkit.webserver.sites;

import com.crikkit.webserver.exceptions.SiteAlreadyInitializedException;
import com.crikkit.webserver.exceptions.SiteCouldNotInitializeException;
import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.utils.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class Site {

    private static HashMap<String, Site> siteDirectory = new HashMap<>();

    public static Collection<Site> getSites() {
        return siteDirectory.values();
    }

    public static Optional<Site> getSite(String host) {
        if (siteDirectory.containsKey(host))
            return Optional.of(siteDirectory.get(host));
        else
            return Optional.empty();
    }

    public static void initializeSite(Site site) throws SiteAlreadyInitializedException, SiteCouldNotInitializeException {
        if (!siteDirectory.containsKey(site.host)) {
            boolean createdDefaultFiles = site.createDefaultFiles();
            if (createdDefaultFiles) {
                siteDirectory.put(site.host, site);
                CrikkitLogger.getInstance().info("Successfully initialized site: " + site);
            } else {
                throw new SiteCouldNotInitializeException(site);
            }
        } else {
            throw new SiteAlreadyInitializedException(site);
        }
    }

    public static void removeSite(Site site) {
        siteDirectory.remove(site.host);
    }

    private String host;
    private boolean enabled;

    public Site(String host, boolean enabled) {
        this.host = host;
        this.enabled = enabled;
    }

    private boolean createDefaultFiles() {
        File directory = new File("sites" + File.separator + host + File.separator + "public_html");
        if (!directory.isDirectory()) {
            boolean result = directory.mkdirs();
            if (result) {
                File indexFile = new File("sites" + File.separator + host + File.separator + "public_html" + File.separator + "index.html");
                if (!indexFile.isFile()) {
                    try {
                        FileUtils.exportInternalFile("templates/index.html", indexFile);
                    } catch (FileNotFoundException e) {
                        CrikkitLogger.getInstance().severe(e);
                    }
                    return true;
                }
            } else {
                CrikkitLogger.getInstance().severe("Could not create default directories for site: " + this);
                return false;
            }
        }

        return true;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("host", host);
        jsonObject.put("enabled", enabled);
        return jsonObject;
    }

    public String getHost() {
        return host;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return host + "|" + (enabled ? "enabled" : "disabled");
    }
}
