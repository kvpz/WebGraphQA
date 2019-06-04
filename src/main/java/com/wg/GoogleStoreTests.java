package com.wg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
    Contains the tests to perform on google store webpages. Each test should be for only one webpage. Please write tests
    for one module of the page or address a single issue on the page. If you want to test a set of pages on these
    functions, write a function to perform the deed in class GoogleStoreTestsDriver.

    The functions should be descriptive of what it tests, and only tests.
 */
public class GoogleStoreTests {

    /**
        Returns true if webPage is a 404 (which is not desirable!)

        Reference: https://www.guru99.com/find-broken-links-selenium-webdriver.html

        Bug: this function returns true for the URLs: https://facebook.com and https://twitter.com
     */
    public static boolean Is404(WebPage webPage) {
        boolean status = false;
        HttpURLConnection huc = null;

        try {
            huc = (HttpURLConnection) (new URL(webPage.GetUrl())).openConnection();

            huc.setRequestMethod("HEAD");

            huc.connect();

            int respCode = huc.getResponseCode();

            if(respCode >= 404)
                status = true;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return status;
    }

    public static boolean Is404(String url) {
        boolean status = false;
        HttpURLConnection huc = null;

        try {
            huc = (HttpURLConnection) (new URL(url)).openConnection();

            huc.setRequestMethod("HEAD");

            huc.connect();

            int respCode = huc.getResponseCode();

            if(respCode >= 404)
                status = true;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return status;
    }

    /**
        Check if the href value is assigned an invalid url.
        Invalid values usually seen:
        javascript.void(0);

        Attributes assigned a url/ path
        href
        data-fjsurl
     */
    public static boolean AreLinksValid(File sourceFile) throws IOException {
        boolean status = true;
        Document html = Jsoup.parse(sourceFile, "utf-8");
        Elements hrefs = html.getElementsByAttribute("href");

        for(Element href : hrefs) {
            String hrefValue = href.attr("href");
            if(hrefValue.contains("javascript")) {
                //System.out.println("href: " + href.attr("href"));
                status = false;
            }
        }
        //System.out.println("Testing datafjsurl values");
        Elements datafjsurls = html.getElementsByAttribute("data-fjsurl");
        for(Element datafjsurl : datafjsurls) {
            String value = datafjsurl.attr("datafjsurl");
            if(value.contains("javascript")) {
                System.out.println("data-fjsurl= " + value);
                status = false;
            }
        }

        Elements srcs = html.getElementsByAttribute("src");
        for(Element src : srcs) {
            String value = src.attr("src");
            if(value.contains("javascript")) {
                System.out.println("src= " + value);
                status = false;
            }
        }

        Elements configurls = html.getElementsByAttribute("data-configure-url");
        for(Element configurl : configurls) {
            String value = configurl.attr("data-configure-url");
            if(value.contains("javascript")) {
                System.out.println("data-configure-url= " + value);
                status = false;
            }
        }

        return status;
    }

    /**
        Test if the anchor tag has a valid ref value (i.e. not javascript.void(0))
     */
    public static boolean IsAnchorRefValid(WebPage webPage) {
        boolean status = false;

        return status;
    }

    /**
        Test if the config page indeed belongs to the product.
        There are several ways of testing this.
        Test Method 1: visit the config page, get the title of the product in the config page,

     */
    public static boolean DoesConfigPageMatchProduct() {
        boolean status = false;

        return status;
    }

    /**
     * On the product page, the relative path to the config page occurs twice in the page source, one for the mobile
     * version of the website and the other for desktop because the mobile transaction button is located at the bottom
     * right of the page whereas the desktop transaction button is constrained to the top right of the page.
     * @return
     */
    public static boolean IsConfigPathSameForDesktopAndMobile(String desktopPath, String mobilePath) {
        return desktopPath.equals(mobilePath);
    }

    /**
        This method tests if the config page URL corresponds to the product.
        The test works by collecting the data-configure-url value on the product page and comparing the
        last string in the path to the last string in the path of the Overview page. It is very important
        to compare the path to the overview url because the last string of the URL for the overview page solely
        represents the product. When testing the validity of the config URL assigned to the buy button, always compare
        the last string in the config URL to the last string in the product overview page.

        The webpage argument passed must not be a config page, and its URL should contain the string "product"
        in its path followed by one more string in the path. If the host language query is at the end of the URL,
        this method will remove it.
     */
    public static boolean DoesBuyButtonLeadToConfig(WebPage webPage) {
        boolean status = false;

        String dataConfigUrl;

        return status;
    }

    /**
        Identify URLs that are in plane text that should be hyperlinked.
     */

    /**
        Search for hhtp (insecure) URLs
     */

    /**
         An external link is a a URL that does not belong to the subdomain of the website. These links should be
        anchored to an element with the attribute "target=_blank". This function accepts a web element (anchor) that is
        an external link (very important). This will only test if the external link opens in a new tab, and it will not
        check if the element contains an external link.
     */
    public static boolean ExternalLinkOpensInNewTab(WebElement element){
        boolean status = false;
        try {
            String refLink = element.getAttribute("href"); // get entire href value
            //System.out.println("pathname: " + refPath + "\n" + "href: " + refLink);

            String anchorTarget = element.getAttribute("target");
            if(anchorTarget == null) {
                status = false;
            }
            else if(anchorTarget.isEmpty()) {
                status = false;
            }
            else if(anchorTarget.equals("_blank")) {
                status = true;
            }
        }
        catch(StaleElementReferenceException e){
            System.out.println("Exception in getLinks" + e);
            status = false;
        }

        return status;
    }

    /**
        A buy button is located at the top right corner of a product page. The button does not have the same action for all
        products. https://store.google.com/in/product/chromecast?hl=en-IN the buy button says "Retailers"

        How to identify Buy/ Retailers button:
        - By class: <button class="button primary transaction">

        Check that there is no more than one buy button at the top right of the page.
        The navbar on the pages should have this: <div jsname="l8GMt" class="hidden" data-name="" data-type="PDP_BAR"></div> within
        the outer most element .
     */
    public static boolean TestBuyButton(WebPage webPage) {
        boolean status = false;


        return status;
    }

    /**
        Check if link is javascript.void(0)
        https://store.google.com/tw/product/chromecast?hl=zh-TW (this page should have such a hyperlink in the footer
     */


    /**
        Test that pages have trademark symbols
        Words that need a trademark symbol: MacOS, Mac Os, Mac, iPhone, iPad, iOS (Cisco scandal, applicable?),
        App Store, OSX,

        References:
        https://www.apple.com/legal/intellectual-property/trademark/appletmlist.html
        https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/en-us.aspx

     */

    /**
        Get all the URLs found across the entire website.
        This function can be used by another function that will check if against the valid links file and prevent
        adding the url to a "foreign links data structure."
        The results of this function can also be used to test all URLs for a 404 response ONLY ONCE. All urls that
        produce a 404 can be stored in a special 404Urls file.
     */
    public static HashMap<String, Integer> GetAllLinksFromLocalSources() throws IOException {
        HashMap<String, Integer> urls = new HashMap<>();

        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();
        for(File dir : regions) {
            File[] regionPages = dir.listFiles();

            for(File page : regionPages) {
                if(page.getName().contains("DS_Store")) continue;

                Document html = Jsoup.parse(new File(page + "/source.html"), "utf-8");
                Elements hrefs = html.getElementsByAttribute("href");
                for(Element href : hrefs) {
                    String hrefValue = href.attr("href");
                    if(urls.containsKey(hrefValue)) {
                        Integer counter = urls.get(hrefValue);
                        ++counter;
                        urls.put(hrefValue, counter);
                    }
                    else {
                        urls.put(hrefValue, 1);
                    }
                }
            }
        }

        return urls;
    }



    /**
        Return a page if it contains the set of URLs passed in the argument.
        Returns a hashmap where the keys represent the invalid url and a set of pages with the url
     */
    public static HashMap<String, HashSet<String>> GetPagesWithInvalidUrls(HashSet<String> urls) throws IOException {
        HashMap<String, HashSet<String>> invalidUrlMap = new HashMap<>(urls.size());
        for(String url : urls) {
            invalidUrlMap.put(url, new HashSet<String>());
        }

        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();
        for(File dir : regions) {
            File[] regionPages = dir.listFiles();
            //System.out.println("Current region: " + dir);
            for(File page : regionPages) {
                if(page.getName().contains("DS_Store")) continue;

                Document html = Jsoup.parse(new File(page + "/source.html"), "utf-8");
                Elements hrefs = html.getElementsByAttribute("href");
                for(Element href : hrefs) {
                    String hrefValue = href.attr("href");
                    if(invalidUrlMap.containsKey(hrefValue)) {
                        invalidUrlMap.get(hrefValue).add(MyUtil.CreateURLFromWebpageDirName(page.getName()));
                    }
                }
            }
        }

        return invalidUrlMap;
    }

    //public static HashSet<String> FindEmptyHrefValues() {

    //}

    /**
        This method will print all the URLs found across the website that are not on the sitemap. The sitemap
        URLs are stored within valid_links.dat
     */
    public static void FindMissingSitemapLinks() throws IOException {
        //HashSet<String> allUrls = new HashSet<String>(GetAllLinksFromLocalSources().keySet());
        HashMap<String, Integer> allUrls = GetAllLinksFromLocalSources();
        HashSet<String> sitemapUrls = new HashSet<String>(Files.readAllLines(Paths.get("valid_links.dat")));

        for(String url : sitemapUrls) {
            if(allUrls.containsKey(url) && allUrls.remove(url) > 0) {
                System.out.println(url + " removed");
            }
        }

        System.out.println("URLs remaining");
        for(String url : allUrls.keySet()) {
            System.out.println(url);
        }

    }

}
