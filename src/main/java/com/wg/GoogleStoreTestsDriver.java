package com.wg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
    This class will be the driver for the tests.
 */
public class GoogleStoreTestsDriver {


    /**
        Find the longest (config title, etc) and the page it belongs to.  This is good  know because a page's style
        does not accomodate all lengths of text.  This is especially noticeable in other regions where the text can
        be much longer than in the US.
        This will look for the longest title for all regions
     */
    public static Map.Entry<String, String> FindLongestConfigPageTitle() {
        Map.Entry<String, String> entry = null;

        return entry;
    }




    /**
     Get the title of the product info pages (store.google.com/product/*)
     */
    public static ArrayList<String> GetConfigDataBackUrl() throws IOException {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();
        for(File dir : regions) {
            File[] regionPages = dir.listFiles();
            System.out.println("Region: " + dir.getName() + "\n\n");
            for(File page : regionPages) {
                if(page.getName().contains("DS_Store")) continue;
                File source = new File(page + "/source.html");;
                if(page.getName().contains("config")) {// || page.getName().contains("comconfig")) {
                    System.out.print(page.getName());
                    String title = Jsoup.parse(source, "utf-8").getElementsByAttribute("data-back-url").get(0).attr("data-back-url");
                    System.out.println(" " + title);
                }
            }
        }

        return titles;
    }

    /**
        Get the title of the product info pages (store.google.com/<region>/product/*)
     */
    public static ArrayList<String> GetProductPageTitles() {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();
        System.out.println("Total regions: " + regions.size());

        for(File dir : regions) {
            File[] regionPages = dir.listFiles();
            for(File page : regionPages) {
                if(page.getName().contains("DS_Store")) continue;

                File source = new File(page + "/source.html");
                //String title = GetPageTitle(new File(page + "/source.html"));
                //String title = GetPageMainTitle(source);
                String title = GetPageTitleText(source);
                if(page.getName().contains("product"))
                    System.out.println(MyUtil.CreateURLFromWebpageDirName(page.getName()) + "   " + title);
            }
        }

        return titles;
    }

    public static ArrayList<String> GetPageTitles() {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();
        for(File dir : regions) {
            File[] regionPages = dir.listFiles();
            for(File page : regionPages) {
                if(page.getName().contains("DS_Store")) continue;

                File source = new File(page + "/source.html");
                //String title = GetPageTitle(new File(page + "/source.html"));
                //String title = GetPageMainTitle(source);
                String title = GetPageTitleText(source);

                //titles.add(title);
            }
        }

        return titles;
    }

    public static String GetPageTitleText(File source) {
        String title = null;
        try {
            Document doc = Jsoup.parse(source, "utf-8");
            //Elements titles = doc.getElementsByAttributeValueContaining("class", "title-text");
            Elements titles = doc.getElementsByAttributeValueContaining("id", "main-title");
            if(titles.size() > 0) {
                title = titles.get(0).text();
            }

            if(titles.size() > 1)
                System.out.println("The page has more than one title");
        }
        catch(Exception e) {}

        return title;
    }

    public static String GetPageMainTitle(File source) {
        String title = null;
        try {
            Document doc = Jsoup.parse(source, "utf-8");
            title = doc.getElementById("main-title").attr("id");
            //System.out.println(doc.text()+"\n");
        }
        catch(Exception e) {}

        return title;
    }

    public static String GetPageTitle(File source) {
        String title = null;
        try {
            Document doc = Jsoup.parse(source, "utf-8");
            title = doc.title();
            //System.out.println(doc.text()+"\n");
        }
        catch(Exception e) {}

        return title;
    }

    public static void ListTitlesOfPages() {
        File[] files = WebGraphFile.GetWebPagesDirectories();
        for(int f = 0; f < files.length; ++f) {
            if(files[f].isDirectory()) {
                File sourceFile = new File(files[f] + "/source.html");
                System.out.println(files[f] + "  " + GetPageTitle(sourceFile));
            }
        }
    }



    /**
        Outer loop iterates through each region directory under WebGraph.
        Inner loop iterates through each page directory/ source for the region

     */
    public static void TestIfHrefValuesAreValid() throws IOException {

        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();
        for(File dir : regions) {
            File[] regionPages = dir.listFiles();
            for(File page : regionPages) {
                if(page.getName().contains("DS_Store")) continue;

                boolean status = GoogleStoreTests.AreLinksValid(new File(page + "/source.html"));
                if(status == false) {
                    System.out.println(page);
                    System.out.println(MyUtil.CreateURLFromWebpageDirName(page.getName()));

                }

            }
        }
        //WebPage page = new WebPage();
        //GoogleStoreTests.AreLinksValid(page);
    }

    public static void FindAllUrlsMissingFromSitemap() throws IOException{
        HashSet<String> missingUrls = new HashSet<>();
        //Set<String> usHrefPaths = usMap.keySet();

        // store all valid links in a list to initialize the hashset with the list
        List<String> validLinks = Files.readAllLines(Paths.get("valid_links.dat"));

        // initialize hashset with all the valid links already found
        HashSet<String> validLinksSet = new HashSet<>(validLinks);

        // get all the links that are missing from the sitemap (valid_links.dat)
        for(String regionCode : WebPage.GetAllRegionCodes().keySet()) {
            String hostLanguageQuery = "?hl=";

            // for regions such as canada which have two primary languages
            if(regionCode.length() > 2) {
                hostLanguageQuery = hostLanguageQuery + regionCode;
                regionCode = regionCode.substring(3, 5);
            }
            else {
                hostLanguageQuery = hostLanguageQuery + regionCode;
            }

            // Get all the store.google.com URLs/ paths
            HashMap<String, HashSet<String>> regionMap = WebGraphFile.GetAllHrefValuesFromLocalRegionSources(regionCode);


            for (String path : regionMap.keySet()) {
                if (!path.contains("collection_id") && !path.contains("/cart") && !path.contains(".css") &&
                        !path.contains("account") && !path.contains("orderhistory") &&
                        !path.contains("countrypicker") &&
                        !path.contains("#") &&
                        !validLinksSet.contains("https://store.google.com/" +regionCode + path + hostLanguageQuery))

                    missingUrls.add("https://store.google.com/" + regionCode + path + hostLanguageQuery);
            }
        }

        File resultsFile = new File("urlsMissingFromSitemap.txt");
        FileWriter writer = new FileWriter(resultsFile);
        for(String url : missingUrls) {
            if(url.contains("www.google.com") || url.contains("mannequin")) continue;
            writer.write(url + "\n");
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        GetProductPageTitles();

        //FindAllUrlsMissingFromSitemap();

        //GoogleStoreTests.FindMissingSitemapLinks();

        //TestIfHrefValuesAreValid();

        //GetConfigDataBackUrl();

        //GetProductPageTitles();

/*
        HashMap<String, Integer> urls = GoogleStoreTests.GetAllLinksFromLocalSources();

        HashSet<String> validLinks = null;
        File validLinksFile = new File("valid_links_backup.dat");
        try {
            validLinks = new HashSet<String>(Files.readAllLines(validLinksFile.toPath()));
        }
        catch (Exception e) {
            System.out.println(e);
        }

        //HashMap<String, Integer> foreignUrls = new HashMap<>();
        HashSet<String> foreignUrls = new HashSet<>();
        for(String e : urls.keySet()) {
            for(String l : validLinks) {
                if(!l.contains(e)) {
                    foreignUrls.add(e);
                }
            }
        }




        HashSet<String> invalidHrefHttpUrls = new HashSet<>();
        foreignUrls.forEach(u -> {
            if(!u.contains("store.google.com")) {
                if (u != null && u.contains("http")) { // && !u.contains("itunes") && !u.contains("instagram")) {
                    if (GoogleStoreTests.Is404(u)) {
                        //System.out.println(u);
                        invalidHrefHttpUrls.add(u);
                    }
                }
            }
        });

        */
        /*

        HashMap<String, HashSet<String>> pagesWithInvalidUrls = GoogleStoreTests.GetPagesWithUrls(foreignUrls);
        for(String e : pagesWithInvalidUrls.keySet()) {
           System.out.println(e + " found in");
           for(String p : pagesWithInvalidUrls.get(e)) {
               if(GoogleStoreTests.Is404(p))
                    System.out.println(p);
           }
           System.out.println("");
        }


        /*

        for(String link : validLinks) {
            if(!urls.containsKey(link)) {
                foreignUrls.put(link, urls.get(link));
                System.out.println("wtf: " + link + "   " + urls.get(link));
            }
        }
        */

        //foreignUrls.forEach((k,v) -> System.out.println(k + ", " + v));

        //urls.forEach((k,v) -> System.out.println(k + ", " + v));

        System.exit(0);
    }
}
