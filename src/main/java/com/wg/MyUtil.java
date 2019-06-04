package com.wg;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
    Miscellaneous helpful functions
 */
public class MyUtil {

    public static UUID urlToUUID(String url) {
        return UUID.nameUUIDFromBytes(url.getBytes());
    }

    public static < K extends String, V extends ArrayList<File> >
    HashMap<String, ArrayList<File>> CreateHashMap(String id, ArrayList<File> values) {
        HashMap<String, ArrayList<File>> hashmap = new HashMap<>();
        //hashmap.put
        return hashmap;
    }


    /**
     Get URL from folder name. The reverse action of GenerateID from class WebGraph should be performed, but for now
     this method will create a URL from the webpage directory name as best as possible (99% accurate).
     Every direction begins with "storegooglecom" and return value will be null otherwise

     To avoid having to use this function, it would be a good idea to save the url of the page in the webpage directory
     and retrieve it when needed.
     */
    public static String CreateURLFromWebpageDirName(String dir) {
        //System.out.println("Creating URL from dir: " + dir);
        // if not a store.google.com directory, return null
        if(!dir.contains("storegooglecom"))
            return null;

        String domain = "https://store.google.com/";
        String url = domain;
        String path = dir.substring(14); // get the substring that follows storegooglecom

        String potentialRegion =  "";
        if(dir.length() > 15)
            potentialRegion = dir.substring(14, 16);

        if(WebPage.GetAllRegionCodes().keySet().contains(potentialRegion)) {
            // handle Puerto Rico region code and "product" conflict
            if(potentialRegion.equals("pr") && dir.length() > 16 && dir.charAt(16) == 'o') { } // not a region code
            else if(potentialRegion.equals("ca") && dir.length() > 16 && dir.charAt(16) == 't') {}
            else {
                url += potentialRegion + "/";
                path = dir.substring(16); // append the rest of the dir name
            }
        }
        else
            potentialRegion = "";

        if(dir.equals("storegooglecom")) {
            url = domain;
        }
        else if(dir.contains("category")) {
            String temp = path.substring(8);
            path = "category/" + temp;
            url += path;
        }
        else if(dir.contains("collection")) {
            String temp = path.substring(10);
            path = "collection/" + temp;
            url += path;
        }
        else if(dir.contains("config")) {
            String temp = path.substring(6);
            path = "config/" + temp;
            url += path;
        }
        else if(dir.contains(potentialRegion + "product")) {
            String temp = path.substring(7);
            path = "product/" + temp;
            url += path;
        }
        else if(dir.contains("magazine")) {
            String temp = path.substring(8);
            path = "magazine/" + temp;
            url += path;
        }

        // add localization host language query parameter to url
        if(path.length() > 7 && path.substring(path.length() - 6).contains("hl") && !path.isEmpty()) {
            url = url.substring(0, url.length() - 6);
            url += "?hl=" + path.substring(path.length() - 4, path.length() - 2);
            url += "-" + path.substring(path.length() - 2);
        }

        return url;
    }


    /**
         List all the files within a directory.
     */
    public static List<String> GetDirectoryListing(String path) {
        File f = new File(path);
        if(f.list() == null) {System.out.println(path);}
        return Arrays.asList(f.list());

    }

    /**
        Create a directory.
        @param dirname path for directory.
     */
    public static File CreateDirectory(String dirname) {
        File f = new File(dirname);
        f.mkdir();
        return f;
    }


    /**
     *
     * @param path absolute path to a file directory
     */
    public static Boolean CheckIfFileExists(Path path) {
        ArrayList<String> list = new ArrayList<String>(MyUtil.GetDirectoryListing(path.getParent().toString()));
        return list.contains(path.getFileName().toString());
    }

    public static void log(String s) {
        System.out.println(s);
    }

    /**
     Read webpage URLs from a file
     */
    public static String[] getLinks(String filePath) {
        String[] links = new String[]{};
        java.io.File infile = new java.io.File(filePath);
        return links;

    }

    /**
     Create a FirefoxDriver with options enabled.
     */
    public static FirefoxDriver CreateFFDriver() {
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        firefoxBinary.addCommandLineOptions("--load-images=no");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("permissions.default.image", 2);
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        firefoxOptions.setProfile(firefoxProfile);

        return new FirefoxDriver(firefoxOptions);
    }


    public static void Sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        for(File dir : WebGraphFile.GetAllDirsFromRegion("ca")) {
            System.out.println(dir.getName());
            System.out.println(CreateURLFromWebpageDirName(dir.getName()));
        }
    }
}

