package com.wg;

import com.google.common.collect.ListMultimap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
    This class manages reading and writing Graph data to and from files. Think of it like a database.

    The file format for a web graph is ParentDir > PageDir > {PageSource, PageDataFile[*]}
    Each page, uniquely determined by its url, will have its own directory. Most of the large objects will be stored
    within its own file such as the page source and member data structures.

    The class will be able to create and initialize webpage objects. The following files represent a Webpage object:
    source.html (page source)
    modules.dat (module ids)

    This class does not perform requests for a new page. WebGraphFileDriver will contain the code for making requests.
    This class can help the driver by providing it with the last date a page source was downloaded.
 */
public class WebGraphFile {

    private static final String WEBDOMAIN= "store.google.com";
    private static final String WEBGRAPHFILE_PATH = "WebGraph" + File.separator; // the location where scraped data is stored
    private String webPageDirPath;
    private String sourceFilePath;
    private String textFilePath;


    public WebGraphFile() {
        webPageDirPath = sourceFilePath  = null;

    }

    public static String GetWebsiteDomain() {
        return WEBDOMAIN;
    }

    /**
        The initializes the path member data where the webpage data should be.
     */
    public WebGraphFile(WebPage webPage) {
        webPageDirPath =  WEBGRAPHFILE_PATH + webPage.GetRegion() + File.separator + webPage.GetId() + File.separator;
        sourceFilePath = webPageDirPath + "source.html";
        textFilePath = webPageDirPath + "text.txt";
    }

    /**
        Return a File array representing all the region directories.
     */
    public static File[] GetRegionDirectories() {
        File[] dirs = null;

        try {

        }
        catch(Exception e) {

        }

        return dirs;
    }


    /**
     * This function attempts to find buy buttons on a page based on certain criteria.
     * Does the element contain a reference to the config or cart page? If so, what is the text associated
     * with the element and what page does it belong to?
     * Returns a map of pages, and each page contains a map of links and their associated text
     */
    public static HashMap<String, HashMap<String, List<String>>> FindBuyLinks() {
        HashMap<String, HashMap<String, List<String>>> regionalBuyLinksMap = new HashMap<>();
        // iterate through all region folder
        // iterate through all pages within a region folder
        // call Find BuyLink(pageSource) for each page (returns HashMap<String, List<String>>)

        return regionalBuyLinksMap;
    }

    /**
     * The only possible keys are /config/<product> or /cart
     * @param pageSource
     * @return a map of buy links and their associated text
     */
    public static HashMap<String, List<String>> FindBuyLinks(File pageSource) {
        HashMap<String, List<String>> pageBuyLinks = new HashMap<>();
        // find the buy links and add the link and associated text to map
        return pageBuyLinks;
    }

    /**
        Get all the directories in WebGraph.
        Updated 2019-04-25 - Deprecated
     */
    public static File[] GetWebPagesDirectories() {
        File[] files = null;
        try {
            File webGraphDir = new File(WEBGRAPHFILE_PATH);
            files = webGraphDir.listFiles();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return files;
    }


    /**
     Get the value of all href attributes contained within the local page sources files.
     The return value will be a map with an href value as key, and a value of a set of pages which contain
     that URI.
     */
    public static HashMap<String, HashSet<String>> GetAllHrefValuesFromLocalRegionSources(String region) throws IOException {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();

        ArrayList<File> regions = GetAllRegionDirs();

        for (File dir : regions) {
            File[] regionPages = dir.listFiles();

            for (File page : regionPages) {
                // remove DS_Store from Mac directories
                if (page.getName().contains("DS_Store"))
                    continue;

                Document html = Jsoup.parse(new File(page + "/source.html"), "utf-8");
                Elements hrefs = html.getElementsByAttribute("href");

                // iterate through pages href tags
                for (Element href : hrefs) {
                    String hrefValue = href.attr("href");

                    // make sure that the path is one that respects robots.txt and is valid
                    if(dir.getName().equals(region) &&
                            hrefValue.length() > 1 && hrefValue.charAt(0) == '/' &&
                            (page.getName().contains(region + "config") ||
                                    page.getName().contains(region+"product") ||
                                    page.getName().contains(region+"magazine"))) {

                        if(!map.containsKey(hrefValue)) {
                            HashSet<String> newPageSet = new HashSet<>();
                            map.put(hrefValue, newPageSet);
                            map.get(hrefValue).add(page.getName());
                        }
                        else {
                            map.get(hrefValue).add(page.getName());
                        }

                    }
                }
            }
        }

        return map;
    }

    /**
     *
     * This function should only be used for verification purposes.
     */
    public static ArrayList<String> GetAllRegions() {
        ArrayList<String> regions = new ArrayList<String>(Arrays.asList((new File("WebGraph").list())));
        regions.removeIf(d -> d.contains("DS_Store"));
        Collections.sort(regions);
        return regions;
    }

    /**
        Returns an array list of file objects pointing to a directory dedicated to a region.
     */
    public static ArrayList<File> GetAllRegionDirs() {
        ArrayList<File> regionDirs = new ArrayList<File>(Arrays.asList(new File("WebGraph").listFiles()));
        regionDirs.removeIf(d -> d.getName().contains("DS_Store"));
        Collections.sort(regionDirs);
        return regionDirs;
    }

    /**
     * The regions that are accepted as an argument can be found under class WebPage (private member regions).
     * @param region The region for which to retrieve files from
     * @return A list of files representing the page directories for that region
     */
    public static ArrayList<File> GetAllDirsFromRegion(String region) {
        // check if region argument is valid
        if(!WebPage.GetAllRegionCodes().containsKey(region))
            return new ArrayList<File>();

        ArrayList<File> files = new ArrayList<File>(Arrays.asList(new File("WebGraph/" + region).listFiles()));
        files.removeIf(d -> d.getName().contains("DS_Store"));
        Collections.sort(files);
        return files;
    }

    /**
        Returns the webpage dedicated directory file path if it exists, else returns null;
     */
    public String GetWebPageFilePath() {
        File file = new File(webPageDirPath);
        if(file.exists()) {
            return file.getAbsolutePath();
        }

        return null;
    }

    public String GetWebPageSourceFilePath() {
        File file = new File(sourceFilePath);
        if(file.exists()) {
            return file.getAbsolutePath();
        }

        return null;
    }

    public static HashSet<String> GetUniqueSitemapUrls() {
        HashSet<String> urls = new HashSet<String>();
        File sitemapFile = new File("sitemap.xml");
        if(!sitemapFile.exists()){
            System.out.println(sitemapFile + " does not exist");
            return null;
        }

        try {
            Document sitemapDoc = Jsoup.parse(sitemapFile, "utf-8");
            Elements elementsWithHref = sitemapDoc.body().getElementsByAttribute("href");
            elementsWithHref.forEach(element -> urls.add(element.attr("href")));
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return urls;
    }

    /**
     Get the URLs generated from the folder names in WebGraph that contain product, collection, and category in the name.
     This returns a sorted array list. It is assumed that the directory WebGraph is populated.
     */
    public static ArrayList<String> GetUrlsFromFileNames() {
        File[] files = WebGraphFile.GetWebPagesDirectories();
        ArrayList<String> urls = new ArrayList<String>();
        for(int f = 0; f < files.length; ++f) {
            String pageUrl = MyUtil.CreateURLFromWebpageDirName(files[f].getName());
            if(pageUrl != null && (pageUrl.contains("product") || pageUrl.contains("collection") || pageUrl.contains("category")))
                urls.add(pageUrl);
        }
        Collections.sort(urls);

        return (urls);
    }

    /**
     Find webpages that have the same module (id)
     */
    public static List<String> GetPagesWithModuleId(String id) {
        ArrayList<String> pages = new ArrayList<>();
        ListMultimap<String, String> modules = Modules.GetModules();
        modules.forEach((pageDir, idVal) -> {
            //System.out.println(pageDir);
            if(idVal.equals(id)) {
                pages.add(MyUtil.CreateURLFromWebpageDirName(pageDir));
            }
        });

        return pages;
    }

    /**
     Get the pixel 3 overview page source for all regions.

     While getting the overview pages, also get the links for the other subpages.
     If the file exists locally, open the file instead of requesting the page with web driver.
     */
    public static HashMap<String, File> GetPixel3PagesOverviews() {
        HashMap<String, File> pixel3Overviews = new HashMap<>();
        HashMap<String, File> pixel3pages = GetPageDir("pixel_3");
        pixel3pages.forEach((k,v) -> {
            File pageDir = new File("WebGraph/" + k + "/storegooglecompixel_3");
            if(v.getName().matches(pageDir.getName()))
                pixel3Overviews.put(k, new File(pageDir + "/source.html"));
        });

        return pixel3pages;
    }

    /**
     Get the page directories in a specific region that match the name given. Returns null if directory not found.
     This function is case sensitive, Pixel will not match directories with pixel(lowercase) within.
     */
    public static List<File> GetPageDir(String product, String region) {
        File regionPages = new File("WebGraph/" + region);
        if(!regionPages.exists())
            return null;

        // Create list with all files within the region's directory
        ArrayList<File> pages = new ArrayList<>(Arrays.asList(regionPages.listFiles()));

        // remove files that do not pertain to query term product
        pages.removeIf(p -> !p.toString().contains(product));

        return pages;
    }


    /**
     Get the page directory for all regions that match the product query term. Does not have to be a product.
     Returns a hashset of regions and the related directories within.
     */
    public static HashMap<String, File> GetPageDir(String product) {
        HashMap<String, File> pages = new HashMap<>();
        ArrayList<File> regions = new ArrayList<>(Arrays.asList(new File("WebGraph/").listFiles()));
        for(File region : regions) {
            if(region.listFiles() == null) continue; // no files, continue to next region
            ArrayList<File> regionDirs = new ArrayList<>(Arrays.asList(region.listFiles()));
            regionDirs.removeIf(pageDir -> pageDir.getName().contains(product) == false);
            regionDirs.forEach(d -> {
                if(d.getName().contains(product))
                    pages.put(region.getName(), d);
            });
        }

        return pages;
    }

    /**
     Get all the text for the Pixel 3 pages (prints to output stream)
     */
    public static void GetPixel3Text() {

        // Get all the text for the pixel 3 pages
        HashMap<String, File> pixel3Pages = GetPageDir("pixel_3");
        for(Map.Entry<String, File> f : pixel3Pages.entrySet()) {
            File sourceFile = new File(f.getValue().getPath() + "/source.html");
            try {
                Document sourceDoc = Jsoup.parse(sourceFile, "utf-8");

                sourceDoc.getElementsByTag("href");

            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
    }

    public static ArrayList<File> GetConfigPages(String region) {
        ArrayList<File> configs = new ArrayList<>(Arrays.asList((new File("WebGraph/" + region)).listFiles()));
        Collections.sort(configs);

        configs.removeIf(d -> !d.getName().contains("storegooglecom" + region + "config"));
        configs.removeIf(d -> d.getName().contains("DS_Store"));

        return configs;
    }

    public static ArrayList<File> GetConfigPages() {
        ArrayList<File> configs = new ArrayList<>();

        for(String region : WebPage.GetAllRegionCodes().keySet()) {
            configs.addAll(GetConfigPages(region));
        }

        return configs;
    }

    /**
        This function relies on the dir names under WebGraph/us. Returns the name of this dir.
     */
    public static ArrayList<File> GetProductsPages(String region) {
        ArrayList<File> products = new ArrayList<>(Arrays.asList((new File("WebGraph/" + region)).listFiles()));

        String filenameExpTemp = "storegooglecom" + region + "product";

        // remove dash from en-hk, fr-be, fr-ca, fr-ch
        if(region.contains("-")) {
            String country = region.substring(3, 5);
            filenameExpTemp = "storegooglecom" + country + "product";
        }

        String filenameExp = filenameExpTemp;

        products.removeIf(d -> !d.getName().contains(filenameExp));
        products.removeIf(d -> d.getName().contains("DS_Store"));

        Comparator c = Collections.reverseOrder();
        products.sort(c);
        //products.sort

        return products;
    }

    /**
     * Get the product pages for all regions
     * @return
     */
    public static ArrayList<File> GetProductPages() {
        ArrayList<File> productPages = new ArrayList<>();
        TreeSet<String> regions = new TreeSet<>(WebPage.GetAllRegionCodes().keySet());

        for(String region : regions) {//WebPage.GetAllRegionCodes().keySet()){
            productPages.addAll(GetProductsPages(region));
        }

        return productPages;
    }

    /**
     *
     * @param regionCode the region for which the config paths will be recorded.
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws NullPointerException when the region code passed as an argument does not exist
     */
    public static HashMap<String, HashSet<String>> GetConfigUrlFromProductPages(String regionCode) throws IOException, URISyntaxException, NullPointerException {
        HashMap<String, HashSet<String>> configUrls = new HashMap<>();

        for(File pageDir : GetAllDirsFromRegion(regionCode)) {
            String parentPageDirName = pageDir.getName();

            // we only want to observe the product pages which most contain a link to config page
            if(parentPageDirName.contains("config") || parentPageDirName.equals("storegooglecom")|| parentPageDirName.equals("storegooglecom" + regionCode))
                continue;
            String parentPageUrl = MyUtil.CreateURLFromWebpageDirName(parentPageDirName);

            // we only want the /product pages since the point is to generate the config from it
            if(!parentPageDirName.contains("product"))
                continue;

            // get the value assigned to data-config-url
            File pageSource = new File(pageDir.getPath() + "/source.html");

            Document source = Jsoup.parse(pageSource, "utf-8");

            // iterate through corresponding attribute values from the page source (there should be 2 per product page (mobile/ desktop))
            Elements configurls = source.getElementsByAttribute("data-configure-url");
            configurls.addAll(source.getElementsByAttribute("data-cta-target-url"));
            if(configurls.size() < 2 || configurls.size() > 2)
                System.out.println("Total config urls found in " + pageDir + ": " + configurls.size() + " which is unusual");

            for(Element configurl : configurls) {
                String configUrl;

                String hrefValue = null;
                if(configurl.hasAttr("data-configure-url"))
                    hrefValue = configurl.attr("data-configure-url");
                else if(configurl.hasAttr("data-cta-target-url"))
                    hrefValue = configurl.attr("data-cta-target-url");

                if(!hrefValue.contains("/config")) {
                    System.out.println(hrefValue + " does not link to config. On page " + parentPageUrl);
                    continue;
                }

                // create the config url (some regions contain 2 languages. create accordingly)
                if(regionCode.length() == 5)
                    configUrl = WEBDOMAIN + "/" + regionCode.substring(3) + hrefValue + "?hl=" + regionCode;
                else
                    configUrl = WEBDOMAIN + "/" + regionCode + hrefValue;


                if(!configUrls.keySet().contains(configUrl)) {
                    HashSet<String> parentPages = new HashSet<>();
                    parentPages.add(parentPageUrl);
                    configUrls.put(configUrl, parentPages);
                }
                else {
                    configUrls.get(configUrl).add(parentPageUrl);
                }
            }
        }

        return configUrls;
    }

        /**
            This will generate fully qualified URLs for the config pages. However, not all of them will be valid because some
            products do not require a configuration page.

            This function should serve as a wrapper over the other, more narrow functions concerning only with the config
            URLs for a particular region. This one should focus on calling them and catching their exceptions.

            Returns a hashmap with the string set to the uri path and values are a set of WebPages where the URI was found.
         */
    public static HashMap<String, HashSet<String>> GetConfigUrlFromProductPages() throws IOException, URISyntaxException {
        HashMap<String, HashSet<String>> configUrls = new HashMap<>();

        // Get the directories for each region
        ArrayList<File> regionDirs = new ArrayList<File>(Arrays.asList(WebGraphFile.GetWebPagesDirectories()));

        for(File regionDir : regionDirs) {
            // avoid retrieving this directory on macOS
            if(regionDir.getName().contains("DS_Store"))
                continue;

            System.out.println("Getting config urls for region: " + regionDir);

            configUrls.putAll( GetConfigUrlFromProductPages(regionDir.getName()));
            System.out.println("configUrls size:  " + configUrls.size());

        }

        return configUrls;
    }

    /**
     * Get all the pages that contain tech specs. The tech specs can sometimes be found under product overview page.
     * @return
     */
    public static TreeSet<File> GetTechSpecPages(String region) {
        TreeSet<File> techSpecFiles = new TreeSet<>();
        for(File file : new TreeSet<File>(GetAllDirsFromRegion(region))) {
            if(file.getName().contains("_specs")) {
                techSpecFiles.add(file);
                continue;
            }

            try {
                Document doc = Jsoup.parse(new File(file + "/source.html"), "utf-8");
                Elements specsElement = doc.getElementsByAttribute("accordion-tech-specs");
                specsElement.addAll(doc.getElementsByAttribute("accordion-tech-specs-simplified"));
                if(specsElement.size() > 1) {
                    System.out.println("There is more than one tech specs container in file " + file + "(" + MyUtil.CreateURLFromWebpageDirName(file.getName()) + ")");
                }
                else if(specsElement.size() == 1) {
                    techSpecFiles.add(file);
                }
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }

        return techSpecFiles;
    }

    /**
     * Get all the pages that contain tech specs. The tech specs can sometimes be found under product overview page.
     * @return
     */
    public static TreeSet<File> GetTechSpecPages() {
        TreeSet<File> techSpecFiles = new TreeSet<>();

        for(String r : WebPage.GetAllRegionCodesSorted()) {
            techSpecFiles.addAll(GetTechSpecPages(r));
        }

        return techSpecFiles;
    }

    /**
        Get the footer content for all pages within the region.
        Returns a hashmap with keys representing pages nd values representing the footers.
     *
    public static HashMap<String, String> GetFooters(String region) {
        HashMap<String, >
    }
    */
    /**
        Persist graph data to a file. This function will overwrite a file if it exists by default.
     */
     public void WriteWebPageToFile(WebPage webPage) {

        try {
            System.out.println("Trying to write for: " + webPage.GetUrl() + " at " + sourceFilePath);
            File pageFile = new File(webPageDirPath);
            if(!pageFile.mkdir())
                System.out.println(pageFile + " was not created");

            File sourceFile = new File(sourceFilePath);

            if(!sourceFile.exists()) {
                System.out.println(sourceFile  + " does not exist");
                boolean created = sourceFile.createNewFile();
                System.out.println("created: " + Boolean.toString(created));
            }
            else
                Files.deleteIfExists(Paths.get(sourceFilePath));

            FileWriter pageSourceOut = new FileWriter(sourceFile, false);

            pageSourceOut.write(webPage.GetPageSource());

            pageSourceOut.close();

        }
        catch(Exception e) {
            System.out.println("Exception in WriteWebPageToFile(WebPage)\n" + e);
            System.out.println(e.getStackTrace());
        }

    }

    public void WriteWebPageToFile(WebPage webPage, String region) {
         try {
             File pageFile = new File(WEBGRAPHFILE_PATH + region + File.separator + webPageDirPath);
             pageFile.mkdir();
             File pageSourceFile = new File(sourceFilePath);
             pageSourceFile.createNewFile();
             FileWriter pageSourceOut = new FileWriter(pageSourceFile);
             pageSourceOut.write(webPage.GetPageSource());

             System.out.println("writing to " + sourceFilePath);
             pageSourceOut.close();
         }
         catch(Exception e) {
            System.out.println(e);
            System.out.println(e.getStackTrace());
         }
    }

    /**
        Write all the text to a file.  The text will not contain any html. A file with all the text
        found on the page is good to have because you can quickly read over the content (out of context)
        and determine if the copy is in the wrong language, spelling errors, missing trademarks, finding
        hidden text, etc.
     */
    public void WriteWebPageTextToFile() {
         try {
             File textFile = new File(textFilePath);
             FileWriter textFileOut = new FileWriter(textFilePath, false);
             textFileOut.write(Jsoup.parse(textFile, "utf-8").text());
             textFileOut.close();
         }
         catch(Exception e) {
             System.out.println(e);
         }
    }
}
