package com.fqa;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import java.util.*;
import java.io.*;
import java.util.Scanner;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Main {
    static String PROXY = "localhost:8080";
    static String website = "https://store.google.com";

    // don't care where YMABI elements belong to as long as they are proper
    String[] module_ymabi_elements;
    public static String[] getLinks(String filePath) {
        String[] links = new String[]{};
        java.io.File infile = new java.io.File(filePath);
        return links;

    }

    public static List<WebElement> getLinks(FirefoxDriver driver) {
        return driver.findElementsByTagName("a");
    }

    public static List<WebElement> getLinks(String html, WebDriver wd) {
        wd.get(html);

        return new ArrayList<>();
    }

    public static String getHtml(String link) {
        String filePath ="";
        try (java.io.PrintWriter out = new java.io.PrintWriter(filePath)) {

        }
        catch(Exception e) {
            System.out.println(e);
            System.out.println("going to create file");
            getLinks(filePath);
        }
        return new String();
    }

    public static void loadURLs(Hashtable<UUID, WebPage> pages, String filePath) {
        // load file with all URLs
        // HTML is only loaded explicitly
        File infile = new File(filePath);
        if(!infile.exists()) {
            System.out.println("No URLs to load. Creating file " + filePath);
            return;
        }

        try {
            Scanner sc = new Scanner(infile);
            while(sc.hasNextLine()) {
                String url = sc.nextLine();
                UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
                System.out.println(uuid);
                pages.put(MyUtil.urlToUUID(url), new WebPage(url));
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void loadURLs(Hashtable<UUID, WebPage> pages, Path path) {
        List<String> files = MyUtil.getDirectoryListing(path.toString());
        files.forEach(F -> {
            WebPage page = new WebPage();
        });
    }

    /*
        path: absolute path to file
     */
    public static Boolean checkIfFileExists(Path path) {
        ArrayList<String> list = new ArrayList<String>(MyUtil.getDirectoryListing(path.getParent().toString()));
        return list.contains(path.getFileName().toString());
    }

    public static void backupPages(Hashtable<UUID, WebPage> pages, String fileName) {
        File outFile = new File(fileName);
        FileWriter fw;
        if(outFile.exists()) {

        }

        try {

            //if (fw.)
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /*
        This will create and add a page to the hashtable. The page object will only have
        the url member data initialized.
     */
    public static void AddPage(Hashtable<UUID, WebPage> pages, String url) {
        WebPage page = new WebPage(url);
        if(!pages.containsKey(MyUtil.urlToUUID(url))) {
            pages.put(MyUtil.urlToUUID(url), page);
        }
    }

    public static void AddPage(Hashtable<UUID, WebPage> pages, WebPage page) {
        AddPage(pages, page);
    }

    /*
        Store links found on a page in hashtable
     */
    public static void StorePageLinks(FirefoxDriver ffdriver, Hashtable<UUID, WebPage> ds) {
        for(WebElement anchor : getLinks(ffdriver)) {
            try {
                String hrefUrl = anchor.getAttribute("href");
                if (hrefUrl == null) {
                    continue;
                }
                if(hrefUrl.contains("smart_light_bundles")) { System.out.println("SMART_LIGHT_BUNDLES!"); }
                AddPage(ds, hrefUrl);
            }
            catch(StaleElementReferenceException e) {
                System.out.println(e);
            }

        }
    }

    /*
        Visit the page located at url and gather info.

    */
    public static void VisitPage(FirefoxDriver fd, Hashtable<UUID, WebPage> pages, WebPage page) {
        fd.get(page.url);
        StorePageLinks(fd, pages);
        page.SetPageSource(fd.getPageSource());
        page.visited = true;
    }

    /*
        Visit pages stored in memory
     */
    public static void VisitPage(FirefoxDriver fd, Hashtable<UUID, WebPage> pages, WebPage page, String loc) {
        System.out.println("VisitPage - cached");

        VisitPage(fd, pages, page);  // temporary
    }

    public static void WriteHTMLToFile(String path, String filename, Hashtable<UUID, WebPage> pages) {
        try {
            File outFile = new File(path + filename);
            if (!outFile.exists()) // do not overwrite existing files
            {
                FileWriter fw = new FileWriter(outFile);
                fw.write(pages.get(UUID.fromString(filename)).GetPageSource());
                fw.close();
            }
        } catch (Exception e) {
            System.out.println("Custom error message (in main)" + e);
        }
    }

    static FirefoxDriver CreateFFDriver() {
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

    public static void main(String[] args) {
        Boolean caching = true;
        String HTMLPagesDir = "/Users/kevin/Documents/googleStore/googleStorePages/";

        WebSiteGraph webGraph = new WebSiteGraph("store.google.com");

        Hashtable<UUID, WebPage> pages = new Hashtable();

        FirefoxDriver fd = CreateFFDriver();

        WebPage startPage = new WebPage(website);
        AddPage(pages, startPage);

        ArrayList<UUID> uuidList;
        do {
            // Visit links from previous page
            uuidList = new ArrayList<UUID>(pages.keySet());
            for (int i = 0; i < uuidList.size(); ++i) {
                UUID currentUUID = uuidList.get(i);
                WebPage currentPage = pages.get(currentUUID);

                // Visit pages under store.google.com
                if (currentPage.url.contains("store.google.com") && currentPage.visited == false) {
                    MyUtil.log("Visiting " + currentPage.url);
                    if (caching && checkIfFileExists(Paths.get(webGraph.graphDir + "/" + currentUUID)))
                        VisitPage(fd, pages, currentPage, "file://" + HTMLPagesDir + "/" + currentUUID);
                    else
                        VisitPage(fd, pages, currentPage);

                    WriteHTMLToFile(HTMLPagesDir, currentUUID.toString(), pages);
                }
            }

        } while(uuidList.size() != pages.size());

        // Gather info about HTML pages
        // Test all Learn More button
        // Test all You May Also Be Into links
        HTMLAnalysis ha;

        //backupPages(pages, urlsFile);
    }
}
