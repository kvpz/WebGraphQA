package com.wg;

import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

/**
    This class is intended to perform actions on the WebGraph. Think database engine.
    The class will be used to perform queries against the WebGraph folder. This should not be used
    extensively because it stresses the hard drive, and performance will be low if the intention is
    to perform many read/ write operations. This class is perfect for reading data from the WebGraph
    directory once and storing the data into memory. To store the data in memory, use the WebPage class.

    This class will make use of Jsoup for parsing the html and storing the desired elements and other data
    within WebPage objects.

 */
public class WebGraphFileDriver {
    private static HashSet<String> validLinks;
    private static File validLinksFile;

    /**
        Careful, this can delete at a whim. RegEx not yet supported.
     */
    static void DeletePageDirectory(String query) {
        WebGraphFile.GetAllRegionDirs().forEach(pdir -> {
            if(pdir != null && pdir.list().length > 0)
            for(File f : pdir.listFiles()){
                if(f.toString().contains(query) && !f.toString().contains("WebGraph/us")) {

                    try { Files.delete(f.toPath()); } catch (Exception e) {System.out.println(e); }
                }
            }
        });
    }

    /**
     <div jscontroller="Q7UYhe" class="mannequin" data-title="RQ Video Player WMBT US (Mannequin)" data-tracking-list-pos="6" data-tracking-module-name="RQ Video Player WMBT US" jsaction="rcuQ6b:rcuQ6b">

     class="wombat_video_player video-player dark-background in-view" seems to be common with all modal video players

     */
    public static List<String> GetPagesWithVideos() {
        List<String> pages = new ArrayList<>();

        return pages;
    }

    /**
        Get all the directories belonging to a region.
     */
    public static ArrayList<File> GetRegionDirs(String region) {
        ArrayList<File> dirs = new ArrayList<>();

        // Check if argument is valid
        if(!WebPage.GetAllRegionCodes().keySet().contains(region)) {
            System.out.println("Invalid region");
            return dirs;
        }

        File regionFile = new File("WebGraph/" + region + "/");
        if(regionFile.listFiles() != null) {
            dirs = new ArrayList<File>(Arrays.asList(regionFile.listFiles()));
        }

        return dirs;
    }


    /**
        Request all pixel 3 pages using a webdriver and persist the source.
        This class relies on the directory names under WebGraph. If directory names are refactored, so must this
        function be.

        DEPRECATE
     */
    public static HashMap<String, File> RequestPixel3PagesOverviews(WebDriver wd) {
        HashMap<String, File> pixel3Overviews = WebGraphFile.GetPixel3PagesOverviews();
        HashMap<String, File> pixel3pages = WebGraphFile.GetPageDir("pixel_3");
        for(String region : WebGraphFile.GetAllRegions()) {
            String url = "https://store.google.com/";
            String regionPath = region;
            if(region.contains("-")) {
                url += region.substring(3) + "/product/pixel_3?hl=" + region;
            }
            else {
                url += regionPath + "/product/pixel_3";
            }

            wd.get(url);
            if(!wd.getCurrentUrl().equals(url)) { // if redirect, pixel 3 page doesn't exist for region
                wd.close();
                continue;
            }

            WebPage webPage = new WebPage(url);
            webPage.SetPageSource(wd.getPageSource());
            WebGraphFile webGraphFile = new WebGraphFile(webPage);
            webGraphFile.WriteWebPageToFile(webPage);
            wd.close();
        }
        return pixel3pages;
    }


    public static void main(String[] args)
            throws InterruptedException, IOException, URISyntaxException {

        // Store valid links in memory for minimizing requests
        validLinksFile = new File("valid_links.dat");
        try {
            validLinks = new HashSet<String>(Files.readAllLines(validLinksFile.toPath()));
        }
        catch (Exception e) {
            System.out.println(e);
        }

        //RequestConfigUrlFromProductPages();

        // Get the config urls from the product pages
        HashMap<String, HashSet<String>> configUrlMap = WebGraphFile.GetConfigUrlFromProductPages();

        for(String key : configUrlMap.keySet()) {
            System.out.println("Key: " + key);
            String[] keyPathVals = key.split("/");

            System.out.println("This config url can be found in the following pages: ");
            for(String val : configUrlMap.get(key)) {
                String[] pathVals = val.split("/");
                String lastPathVal = pathVals[pathVals.length - 1];
                if(!lastPathVal.equals(keyPathVals[keyPathVals.length - 1]))
                    System.out.println(val);
            }
            System.out.println();
        }

        try {
         //   UpdateFromSitemap();
        }
        catch(Exception e) {

        }



        try {
            FileWriter fwriter = new FileWriter(validLinksFile, false);
            for(String url : validLinks) {
                fwriter.write(url + "\n");
            }
            fwriter.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        System.exit(0);
    }
}
