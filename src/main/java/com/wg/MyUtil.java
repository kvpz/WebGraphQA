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

    /*
    // http://gs.statcounter.com/screen-resolution-stats
    // set of viewports (width, height) ordered by width
    public static HashSet<Pair<Integer, Integer>> viewportDimensions = new HashSet<Pair<Integer, Integer>>(){
            {
                add(new Pair<Integer, Integer>(360,640));
                add(new Pair<Integer, Integer>(360,720));
                add(new Pair<Integer, Integer>(375, 667));
                add(new Pair<Integer, Integer>(414, 736));
                add(new Pair<Integer, Integer>(600, 1024));
                add(new Pair<Integer, Integer>(601, 962));
                add(new Pair<Integer, Integer>(720, 1280));
                add(new Pair<Integer, Integer>(768, 1024)); // most common tablet resolution
                add(new Pair<Integer, Integer>(800, 1280));
                add(new Pair<Integer, Integer>(834, 1112));
                add(new Pair<Integer, Integer>(962, 601));
                add(new Pair<Integer, Integer>(1024, 768));
                add(new Pair<Integer, Integer>(1024, 1366));
                add(new Pair<Integer, Integer>(1280, 720));
                add(new Pair<Integer, Integer>(1280, 800));
                add(new Pair<Integer, Integer>(1280, 1024));
                add(new Pair<Integer, Integer>(1366, 768));
                add(new Pair<Integer, Integer>(1440, 900));
                add(new Pair<Integer, Integer>(1536, 864));
                add(new Pair<Integer, Integer>(1600, 900));
                add(new Pair<Integer, Integer>(1920, 1080));
            }
    };
*/
    public static class Timer {
        public Timer() {
            start = new Date();
        }

        public void End() {
            end = new Date();

            System.out.println((end.getTime() - start.getTime()) / 1000f + " seconds");
        }

        private Date start;
        private Date end;
    }

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
        String hostname_concatenated = "storegooglecom";

        //System.out.println("Creating URL from dir: " + dir);
        // if not a store.google.com directory, return null
        if(!dir.contains(hostname_concatenated))
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
            // handle Canada region code and "category" conflict
            else if(potentialRegion.equals("ca") && dir.length() > 16 && dir.charAt(16) == 't') {}
            else {
                url += potentialRegion + "/";
                path = dir.substring(16); // append the rest of the dir name
            }
        }

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
        else if(dir.contains("product")) {
            String temp = path.substring(7);
            path = "product/" + temp;
            url += path;
        }
        else if(dir.contains("magazine")) {
            String temp = path.substring(8);
            path = "magazine/" + temp;
            url += path;
        }
        else if(dir.length() == 16 && dir.contains(potentialRegion)) {
            // url is fine the way it is
        }
        else if(dir.substring(16,18).equals("hl") && dir.contains(potentialRegion)){}
        else {
            // the url path is not valid; return null
            return null;
        }

        // add localization host language query parameter to url
        if(path.length() > 7 && path.substring(path.length() - 6).contains("hl") && !path.isEmpty()) {
            url = url.substring(0, url.length() - 6);
            url += "?hl=" + path.substring(path.length() - 4, path.length() - 2);
            url += "-" + path.substring(path.length() - 2);
        }

        // add host language query string if
        else if(path.length() == 6) {
            url += "?hl=" + path.substring(2,4) + "-" + path.substring(4,6);

        }

        return url;
    }


    /**
         List all the files within a directory.
     */
    public static List<String> GetDirectoryListing(String path) {
        File f = new File(path);
        if(f.list() == null) {System.out.println("NULL: " + path);}
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
        //firefoxBinary.addCommandLineOptions("--load-images=no");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        //firefoxProfile.setPreference("permissions.default.image", 2);
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
        for(File regionDir : WebGraphFile.GetAllRegionDirs()) {//.GetAllDirsFromRegion("ca")) {
            for(File dir : WebGraphFile.GetAllDirsFromRegion(regionDir.getName())) {
                System.out.println(CreateURLFromWebpageDirName(dir.getName()));
            }
        }
    }
}

