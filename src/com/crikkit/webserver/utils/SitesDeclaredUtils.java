package com.crikkit.webserver.utils;

import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.sites.Site;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class SitesDeclaredUtils {

    public static void addSite(Site site) throws IOException {
        File config = new File("configurations" + File.separator + "sites_declared.json");
        if (!config.exists()) {
            throw new FileNotFoundException();
        }

        JSONObject siteObject = new JSONObject();
        siteObject.put("host", site.getHost());
        siteObject.put("enabled", site.isEnabled());

        JSONObject sitesDeclaredObject = new JSONObject(FileUtils.fileToString(config));
        JSONArray sitesArray = sitesDeclaredObject.getJSONArray("sites");
        sitesArray.put(sitesArray.length(), siteObject);

        try (FileWriter file = new FileWriter(config.getPath())) {
            file.write(sitesDeclaredObject.toString());
            file.close();
            CrikkitLogger.getInstance().info("Successfully saved site to sites_declared!");
        }
    }

    public static void deleteSite(Site site) throws IOException {
        File config = new File("configurations" + File.separator + "sites_declared.json");
        if (!config.exists()) {
            throw new FileNotFoundException();
        }

        JSONObject sitesDeclaredObject = new JSONObject(FileUtils.fileToString(config));
        JSONArray sitesArray = sitesDeclaredObject.getJSONArray("sites");
        JSONArray updatedSitesArray = new JSONArray();

        for (int i = 0; i < sitesArray.length(); i++) {
            JSONObject siteObject = sitesArray.getJSONObject(i);
            if (!siteObject.getString("host").equalsIgnoreCase(site.getHost())) {
                updatedSitesArray.put(siteObject);
            }
        }

        sitesDeclaredObject.put("sites", updatedSitesArray);
        try (FileWriter file = new FileWriter(config.getPath())) {
            file.write(sitesDeclaredObject.toString());
            file.close();
            CrikkitLogger.getInstance().info("Successfully updated sites_declared!");
        }
    }

    public static boolean deleteSiteFiles(Site site) {
        File directory = new File("sites" + File.separator + site.getHost());
        if (directory.exists()) {
            return FileUtils.deleteFolder(directory);
        } else {
            return true;
        }
    }

}
