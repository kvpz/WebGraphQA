package com.wg;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.net.URI;
import java.util.*;
import java.io.*;
import java.nio.file.Path;

import org.jgrapht.io.*;
import org.jgrapht.*;

/**
    @class GoogleStoreCrawler
    The folder structure will look as follows:
    < url domain >/< page id >/[pageSource<Date>]
    < url domain >/< page id >/< page id >.data


    < page id >.data structure
    --------------------------
    id:
    url:
    title:
    lastVisited:

    This structure will reduce runtime memory usage and increase performance because the data will not have to be
    parsed from the html.

    How caching will work
    The selenium web driver will be able to load a local html file.
    Every visited webpage will have its own folder. The folder structure will look as follows:
    < url domain >/< page id >/[pageSource< date >]

    If caching is on, the last page source requested will be used. If caching is off, a new file storing the page
    source will be created.

    Please use File.separator everywhere for cross-compatibility. Try to avoid hardcoding slashes.

    Contact me if you want some data retrieved from traversals using this very program.


 */

public class GoogleStoreCrawler {

    /**
        "Global" variables
    */
    static Date DATE = new Date(); // the time this program is executed
    static FileWriter runtimeLogFile = null; // file writer for runtime logging
    static Graph<WebPage, WebPageEdge> webGraph; // the main graph structure
    static String webGraphPath = "WebGraph" + File.separator; // the location where scraped data is stored (moving to class WebGraphFile)
    static boolean overwrite = false; // moving to class WebGraphFile
    static FirefoxDriver fd = CreateFFDriver();

    /**
        Set caching on if requesting the page with a web driver is not desired
     */
    boolean caching = true;

    // The website's homepage
    static final String WEBSITE = "store.google.com";



    /**
        This function will collect all links on a page.
        Issue: if the webpage is stored locally, the relative links paths will be evaluated to local storage.
        To get the href value as seen in the html, do webElement.getAttribute("pathname") assumming the webElement
        represents a anchor element.


        HTML elements that give this function trouble:
        [1] <a href="https://payments.google.com/payments/apis-secure/get_legal_document?ldo=0&amp;ldt=buyertos&amp;ldr=US" target="_blank">Google Payments Terms of Service</a>
            The query string is not recognized.

        [2] <link href="/intl/ALL_us/about/images/store_icon.png" rel="shortcut icon">
            It seems like webElement.getAttribute("pathname") does not work the same way on non-anchor elements (at least link elements)

        [3] <a class="mqn-abf mqn-abp" href="https://support.google.com/store/answer/6380752?hl=en-US" mqn-autotrack-label="https://support.google.com/store/answer/6380752" target="_blank"></a>
            It seems like webElement.getAttribute("pathname") will get the path for the absolute url assigned to href.
     */
    public static List<String> getLinks(FirefoxDriver driver) {
        String[] tags = {"a", "area", "base", "link"};
        String[] attributes = {"href", "data-config-url"};
        List<String> links = new ArrayList<String>();
        for(int _i = 0; _i < 4; ++_i) {
            List<WebElement> linkElements = driver.findElementsByTagName(tags[_i]);

            for(WebElement webElement : linkElements) {
                try {
                    String refLink = webElement.getAttribute("pathname"); // get the path of the href value

                    if (refLink != null && !refLink.equals("#")) {
                        if (!webElement.getAttribute("href").contains("storage.google.com")) // avoid appending the incorrect domain to a path
                            links.add("https://" + WEBSITE + refLink);
                    } else {
                        links.add(webElement.getAttribute("href")); // store hard link value assigned to href
                    }
                }
                catch(StaleElementReferenceException e){
                    System.out.println("Exception in getLinks" + e);
                }
            }
        }

        return links;
    }

    /**
        This will create and add a page to the graph.
     */
    public static void AddPageToGraph(Graph<WebPage, WebPageEdge> g, WebPage page) {
        webGraph.addVertex(page);
    }

    /**
        Read URLs from the page source to create and add WebPage vertices into the graph.
        This function prevents adding vertices that do no belong under the store.google.com domain.
     */
    public static void StorePageLinksInGraph(FirefoxDriver fd, WebPage webPage) {
        // Webpage must be under WEBSITE domain
        if(!webPage.GetUrl().contains(WEBSITE)) {
            System.out.println("Webpage " + webPage.GetUrl() + " will not be stored in graph");
            return;
        }

        for(String url : getLinks(fd)) {
            if (url != null) {
                System.out.println("Adding webpage " + url + " to graph");
                WebPage relPage = new WebPage(url);
                webGraph.addVertex(relPage);
                webGraph.addEdge(webPage, relPage, new WebPageEdge("to"));
            }
        }
    }

    /**
        Get the page title (as seen on browser tab) which is within the element with the id "main-title" (except for a few pages like privacy policy)
     */
    public static String GetPageTitle(FirefoxDriver fd) {
        String pageTitle = null;
        try {
            pageTitle = fd.findElement(By.id("main-title")).getText();
        }
        catch(Exception e) {
            System.out.println("Exception in GetPageTitle(FirefoxDriver)\n" + e);
        }

        return pageTitle;
    }

    /**
        This function does not care whether the page has been visited or not. Page will always be requested.
        This function contains the code for parsing/ driving the webpage.
        This function contains a thread sleeper to provide time for a page module to load
        This function populates a webpage object.
     */
    public static void VisitPage(FirefoxDriver fd, Graph<WebPage, WebPageEdge> g, WebPage page) {
        fd.get(page.GetUrl());

        // Extract links in the page
        page.SetPageSource(fd.getPageSource());
        page.SetTotalDOMNodes(fd.findElements(By.cssSelector("*")).size());
        page.SetTitle(GetPageTitle(fd));

        List<WebElement> pageModules = fd.findElements(By.className("page-module"));
        page.AddModules(pageModules);


        page.SetVisited();
    }

    /**
        Write the page source to a file on disk.
        The file is stored in the Pages directory.

        Moving to class WebGraphFile (possibly renaming to WritePageSource)
     */
    public static void WriteHTMLToFile(String fileName, Hashtable<UUID, WebPage> pages) {
        String path = webGraphPath;
        File pageDir = MyUtil.CreateDirectory(path + fileName);
        try {
            File outFile = new File(pageDir + "/" + fileName);
            if (!outFile.exists()) // do not overwrite existing files
            {
                FileWriter fw = new FileWriter(outFile);
                fw.write(pages.get(UUID.fromString(fileName)).GetPageSource());
                fw.close();
            }
        } catch (Exception e) {
            System.out.println("Custom error message (WriteHTMLToFile())\n" + e);
        }
    }

    /**
        Create a FirefoxDriver with options enabled.
     */
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

    /**
        Creates a directory for a webpage.

        Moving to class WebGraphFile
     */
    static File CreatePageDir(String path, String dirName) {
        return MyUtil.CreateDirectory(path + dirName);
    }



    /**
         Write to the runtime log file.
     */
    static void WriteToLog(String s){
        try {
            runtimeLogFile.write(s);
            runtimeLogFile.flush();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
        Create runtime log file that chronologically logs what pages are visited.
    */
    private static void InitializeSessionLog() {
        String sessionLogPrefix = "sessionlog_";
        try {
            runtimeLogFile = new FileWriter("WebGraph" + File.separator + sessionLogPrefix + DATE.getTime() + ".txt");
        }
        catch(Exception e) {
            System.out.println("Issue opening log file\n" + e);
        }
    }

    /**
        Moving to WebGraphFile
     */
    public static void ExportGraph() {
        // Export graph
        ComponentNameProvider<WebPage> vertexIdProvider = new ComponentNameProvider<WebPage>()
        {
            public String getName(WebPage page)
            {
                return page.GetId();
            }
        };
        ComponentNameProvider<WebPage> vertexLabelProvider = new ComponentNameProvider<WebPage>()
        {
            public String getName(WebPage page)
            {
                return page.GetUrl();
            }
        };
        ComponentNameProvider<WebPageEdge> edgeLabelProvider = new ComponentNameProvider<WebPageEdge>() {
            @Override
            public String getName(WebPageEdge component) {
                return component.getLabel();
            }
        };

        GraphExporter<WebPage, WebPageEdge> exporter =
                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, edgeLabelProvider);
        Writer writer = new StringWriter();
        try {
            exporter.exportGraph(webGraph, writer);
            System.out.println(writer.toString());
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
        Get the domain of the URL.
     */
    public static String GetUrlDomain(String url) {
        String host = null;
        try {
            URI uri = new URI(url);
            host = uri.getHost();
        }
        catch(Exception e) {
            System.out.println("Exception in GetUrlDomain(String)\n" + e);
        }

        return host;
    }

    /**
        Only visit (request) pages with store.google.com domain. Other url domains are child vertices and should not be downloaded.
     */
    public static void TraverseGraph(GraphIterator webGraphItr) {

        do {
            WebPage webPage = (WebPage) webGraphItr.next();
            System.out.println("Current traversal page: " + webPage.GetUrl());

            AddPageToGraph(webGraph, webPage); // create a vertex

            if(!webPage.GetVisited() && GetUrlDomain(webPage.GetUrl()).contains(WEBSITE) &&
                webPage.GetPageSourcePath() == null) {

                System.out.println("Visiting page: " + webPage.GetUrl());
                VisitPage(fd, webGraph, webPage);

                WebGraphFile pageOutFile = new WebGraphFile(webPage);
                pageOutFile.WriteWebPageToFile(webPage);

            }
            else if(GetUrlDomain(webPage.GetUrl()).contains(WEBSITE) && webPage.GetPageSourcePath() != null) {
                System.out.println("Getting page source: " + webPage.GetPageSourcePath());
                fd.get("file:///" + webPage.GetPageSourcePath());
            }

            System.out.println("Adding links to graph from " + webPage.GetUrl());
            StorePageLinksInGraph(fd, webPage);

        } while(webGraphItr.hasNext());
    }


    public static void Traverse() {
        GraphIterator webGraphItr = new BreadthFirstIterator<WebPage, WebPageEdge>(webGraph);

        int graphSize;
        do {
            System.out.println("Outer loop (main) 1");
            graphSize = webGraph.vertexSet().size();
            TraverseGraph(webGraphItr);
            System.out.println("Outer loop (main) 2");
            webGraphItr = new BreadthFirstIterator<WebPage, WebPageEdge>(webGraph);
            System.out.println("Outer loop (main) 3");
        }while(graphSize != webGraph.vertexSet().size());
    }

    /**
        Crawl the entire website.
     */
    public static void main(String[] args) {

        InitializeSessionLog();

        // Instantiate graph
        WebPage startPage = new WebPage("https://store.google.com/ca/product/google_pixel_buds");
        webGraph = new DefaultDirectedGraph(WebPageEdge.class);
        AddPageToGraph(webGraph, startPage); // creates and adds webpage vertex to graph

        // traverse graph
        Traverse();

        System.out.println("Total vertices: " + webGraph.vertexSet().size());
        System.out.println("Total runtime: " + ((new Date()).getTime() - DATE.getTime()) / 1000.0f + "s");

        ExportGraph();

    }
}

