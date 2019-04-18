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
    static boolean overwrite = false; // moving to class WebGraphFile
    static FirefoxDriver fd = CreateFFDriver();
    static HashSet<String> linksFound;

    /**
        Set caching on if requesting the page with a web driver is not desired
     */
    boolean caching = true;

    // The website's homepage
    static final String WEBSITE = "store.google.com";


    /**
     This will create and add a page to the graph.
     */
    public static void AddPageToGraph(Graph<WebPage, WebPageEdge> g, WebPage page) {
        webGraph.addVertex(page);
    }

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
            Link tags are now being avoided.

        [3] <a class="mqn-abf mqn-abp" href="https://support.google.com/store/answer/6380752?hl=en-US" mqn-autotrack-label="https://support.google.com/store/answer/6380752" target="_blank"></a>
            webElement.getAttribute("pathname") will get the path for the absolute url assigned to href.
     */
    public static List<String> getLinks(FirefoxDriver driver) {
        String[] tags = {"a"}; //, "area", "base", "link"};
        //String[] attributes = {"href", "data-config-url"};
        List<String> links = new ArrayList<String>();

        for(int _i = 0; _i < tags.length; ++_i) {
            List<WebElement> linkElements = driver.findElementsByTagName(tags[_i]);

            for(WebElement webElement : linkElements) {
                try {
                    String urlToAdd = null;
                    String refPath = webElement.getAttribute("pathname"); // get the path of the href value
                    String refLink = webElement.getAttribute("href"); // get entire href value
                    System.out.println("pathname: " + refPath + "\n" + "href: " + refLink);

                    if(refPath == null || refLink == null) continue;
                    if(refPath.equals("#")) continue;
                    if(refLink.contains("http") && !refLink.contains(WEBSITE)) { // href value is external link (assumed because it is an absolute path)
                        urlToAdd = refLink;
                    }
                    else { // href value is relative to the website domain
                        urlToAdd = "https://" + WEBSITE + refPath;
                    }

                    links.add(urlToAdd);
                }
                catch(StaleElementReferenceException e){
                    System.out.println("Exception in getLinks" + e);
                }
            }
        }

        return links;
    }

    /**
        Read URLs from the page source to create and add WebPage vertices into the graph.
        This function prevents adding links from a web page that does not belong under the store.google.com domain. It
        will however add webpage links that are found under a valid webpage.
     */
    public static void StorePageLinksInGraph(WebPage webPage) {
        // Webpage must be under WEBSITE domain
        if(!webPage.GetUrl().contains(WEBSITE)) {
            //System.out.println("Webpage " + webPage.GetUrl() + " will not be stored in graph");
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
        This is called by function TraverseGraph
     */
    public static void VisitPage(WebPage page) {
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
        Only visit (request) pages with store.google.com domain and add them to the graph. This includes files that
        are stored locally (which are under store.google.com). Other url domains are child vertices and are not downloaded.
     */
    public static void TraverseGraph(GraphIterator webGraphItr) { //, int traversalLimiter) {
        int startGraphSize = webGraph.vertexSet().size();
        int traversalCounter = 0;

        do {
            WebPage webPage = (WebPage) webGraphItr.next();

            AddPageToGraph(webGraph, webPage); // add a new vertex regardless of webpage domain

            // visit webpage that belongs to store.google.com that has not been visited by traversal
            if(!webPage.GetVisited() && GetUrlDomain(webPage.GetUrl()).contains(WEBSITE) &&
                webPage.GetPageSourcePath() == null) {

                System.out.println("Visiting page: " + webPage.GetUrl());
                VisitPage(webPage);

                // store webpage locally
                WebGraphFile pageOutFile = new WebGraphFile(webPage);
                pageOutFile.WriteWebPageToFile(webPage);
            }
            else if(GetUrlDomain(webPage.GetUrl()).contains(WEBSITE) && webPage.GetPageSourcePath() != null) {
                // request webpage from local file
                fd.get("file:///" + webPage.GetPageSourcePath());
            }

            System.out.println("Adding links to graph from " + webPage.GetUrl());
            if(webPage.GetUrl().contains(WEBSITE)) {
                linksFound.add(webPage.GetUrl());
                StorePageLinksInGraph(webPage); // will not store links if page is not under store.google.com
            }

            System.out.println("Ending traversal iteration. Graph size: " + startGraphSize + " counter: " + traversalCounter);
        } while(++traversalCounter < startGraphSize); // && traversalCounter < traversalLimiter);
        System.out.println("Leaving TraverseGraph()");
    }

    /**
        Outer loop of graph traversal. This creates an iterator for the graph with new elements added.
     */
    public static void Traverse() {//int traversalLimiter)

        GraphIterator webGraphItr = new BreadthFirstIterator<WebPage, WebPageEdge>(webGraph);

        int graphSize;
        int traversalCounter = 0;
        do {
            graphSize = webGraph.vertexSet().size();
            TraverseGraph(webGraphItr); //, traversalLimiter);
            webGraphItr = new BreadthFirstIterator<WebPage, WebPageEdge>(webGraph);
        }while(graphSize != webGraph.vertexSet().size()); // && traversalCounter < traversalLimiter);
    }

    /**
        Crawl the entire website. Tests can be added within Traverse (within any code reached by that function).
     */
    public static void main(String[] args) {

        InitializeSessionLog();

        linksFound = new HashSet<String>();
        FileReader inLinksFile = null;
        FileWriter outLinksFile = null;
        File linksFile = null;
        try {
            linksFile = new File("GStoreLinks.dat");
            inLinksFile = new FileReader(linksFile);
            // Store unique links in hashset
            BufferedReader bufInLinksFile = new BufferedReader(inLinksFile);
            bufInLinksFile.lines().forEach(l -> linksFound.add(l));
            bufInLinksFile.close();

        }
        catch(Exception e) {
            System.out.println(e);
        }



        // Instantiate graph
        WebPage startPage = new WebPage("https://store.google.com/product/pixel_3");
        webGraph = new DefaultDirectedGraph(WebPageEdge.class);
        AddPageToGraph(webGraph, startPage); // creates and adds webpage vertex to graph

        // traverse graph
        Traverse();

        System.out.println("Total vertices: " + webGraph.vertexSet().size());
        System.out.println("Total runtime: " + ((new Date()).getTime() - DATE.getTime()) / 1000.0f + "s");

        try {
            outLinksFile = new FileWriter(linksFile);
            BufferedWriter bufOutLinksFile = new BufferedWriter(outLinksFile);
            for(String link : linksFound) {
                bufOutLinksFile.write(link);
                bufOutLinksFile.newLine();
            }
            bufOutLinksFile.close();
        }
        catch(Exception e){
            System.out.println(e);
        }

        ExportGraph();

    }
}

