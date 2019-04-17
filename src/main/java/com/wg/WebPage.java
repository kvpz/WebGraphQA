package com.wg;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;

/*
    There will be pages that contain modals, and opening the modals extends the url path.

    For a webpage to be created, a url needs to be provided. Every url provided is assumed valid.

 */
public class WebPage {

    public static Map<String, String> regions = new HashMap<String, String>() {
        {
            put("br", "portuguese");
            put("ca", "english");
            put("ca", "french");
            put("mx", "spanish");
            put("pr", "spanish");
            put("au", "english");
            put("hk", "chinese");
            put("hk", "english");
            put("in", "english");

        }
    };
    private String id;
    private String url;
    private String title; // html: id="main-title"
    private String pageSource;
    private Date lastUpdated; // last time object was modified
    private Boolean visited; // true if page source is stored locally
    private int totalLinks;
    private int totalDOMNodes;
    private Map<String, String> modules;
    private String sourceFilePath;

    /***************
        Constructors
     ***************/

    /*
        This default constructor should never be used because a WebPage needs to have a (unique) url.
     */
    private WebPage() {

        visited = false;
        modules = new HashMap<String, String>();
    }

    /*
        Check if webpage can be retrieved locally. The page source will not be stored in the object if
        it has been persisted to a file in order to reduce memory consumption.
     */
    WebPage(WebPage p) {
        this.url = p.url;
        this.id = p.id;
        this.lastUpdated = p.lastUpdated;
        this.visited = p.visited;
        this.totalDOMNodes = 0;
        modules = new HashMap<String, String>();

        // initialize object with previously persisted data
        WebGraphFile webPageDir = new WebGraphFile(this);
        sourceFilePath = webPageDir.GetWebPageSourceFilePath(this);
        if(sourceFilePath != null)
            this.visited = true;
    }

    /**
        Create a webpage with a url. Member data will be initialized if data files exist.
     */
    WebPage(String url) {
        this.url = url;
        this.id = GenerateID(url);
        this.lastUpdated = new Date();
        this.visited = false;
        this.totalDOMNodes = 0;
        modules = new HashMap<String, String>();

        // initialize object with previously persisted data
        WebGraphFile webPageDir = new WebGraphFile(this);
        sourceFilePath = webPageDir.GetWebPageSourceFilePath(this);
        System.out.println("Webpage(url): sourceFilePath: " + sourceFilePath);
        if(sourceFilePath != null)
            this.visited = true;
    }

    /******************
        Public Methods
     ******************/

    /************
        Mutators
     ************/

    public void SetTitle(String title) {
        this.title = title;
    }

    public void SetPageSource(String source) {
        pageSource = source;
    }

    //public void SetPageSourcePath(String path) {     sourceFilePath = path;   }

    public void SetVisited() {
        visited = true;
    }

    public void SetTotalDOMNodes(int total) {
        totalDOMNodes = total;
    }

    public void AddModule(String id, String html) {
        modules.put(id, html);
    }

    public void AddModules(List<WebElement> modules) {
        for (WebElement element : modules) {
            //((JavascriptExecutor) fd).executeScript("arguments[0].scrollIntoView(true);", element);
            try {
                Thread.sleep(500); // allow element to load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!element.getAttribute("id").equals("")) {
                AddModule(element.getAttribute("id"), element.getAttribute("outerHTML"));
            }

        }
    }

    /*************
        Accessors
     *************/

    public String GetId() {
        return id;
    }

    public String GetPageSource() {
        return pageSource;
    }

    public String GetPageSourcePath() {
        return sourceFilePath;
    }

    public String GetTitle() {
        return title;
    }

    public String GetUrl() {
        return url;
    }

    public Date GetLastUpdateDate() {
        return lastUpdated;
    }

    public boolean GetVisited() {
        return visited;
    }

    public Map<String, String> GetModules() {
        return modules;
    }

    public String GetModule(String key) {
        return modules.get(key);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WebPage other = (WebPage) obj;
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }

    /*
        This is required for linking two graph nodes
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(id);
        if (url != null) {
            sb.append(",").append(url);
        }
        sb.append(")");
        return sb.toString();
    }



    /*********************
        Private Functions
    **********************/

    private String GenerateID(String url) {

        String urlid = url;
        if(url.charAt(6) == '/') // remove http:
            urlid = urlid.substring(7);
        else if(url.charAt(7) == '/') // remove https:
            urlid = urlid.substring(8);

        // remove special characters
        urlid = urlid.replaceAll("(\\.)|(/)|(\\?)|(=)|(&)|(-)|(#)|(%)|(\\+)|(')","");

        return urlid;
    }


}
