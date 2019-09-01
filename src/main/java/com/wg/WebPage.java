package com.wg;
import org.openqa.selenium.WebElement;
import java.util.*;

/*
    There will be pages that contain modals, and opening the modals extends the url path.

    For a webpage to be created, a url needs to be provided. Every url provided is assumed valid.

 */
public class WebPage {

    private static HashMap<String, String> regions = new HashMap<String, String>() {
        {
            put("at", "german");
            put("au", "english");
            put("be", "dutch");
            put("br", "portuguese");
            put("ca", "english");
            put("ch", "german");
            put("de", "german");
            put("dk", "danish");
            put("es", "spanish");
            put("fi", "finnish");
            put("fr", "french");
            put("fr-be", "french");
            put("fr-ca", "french");
            put("fr-ch", "french");
            put("gb", "english");
            put("hk", "english");
            put("id", "indonesian");
            put("ie", "english");
            put("in", "english");
            put("it", "italian");
            put("jp", "japanese");
            put("kr", "korean");
            put("mx", "spanish");
            put("my", "english");
            put("nl", "dutch");
            put("no", "norwegian");
            put("nz", "english");
            put("ph", "english");
            put("pr", "spanish");
            put("pt", "portuguese");
            put("se", "swedish");
            put("sg", "english");
            put("th", "thai");
            put("tw", "chinese");
            put("us", "english");
            put("zh-hk", "chinese");
        }
    };


    private String id;
    private String url;
    private String title; // html: id="main-title"
    private String pageSource;
    private String text;
    private Date lastUpdated; // last time object was modified
    private Boolean visited; // true if page source is stored locally (Webpage is treated like a node in a tree)
    private int totalDOMNodes;
    private Map<String, String> modules;
    private String sourceFilePath; // this should only be in class WebGraphFile
    private String region;

    /***************
        Constructors
     ***************/

    /*
        This default constructor should never be used because a WebPage needs to have a (unique) url.
     */
    private WebPage() { }

    /*
        Check if webpage can be retrieved locally. The page source will not be stored in the object if
        it has been persisted to a file in order to reduce memory consumption.
     */
    public WebPage(WebPage p) {
        this.url = p.url;
        this.id = p.id;
        this.lastUpdated = p.lastUpdated;
        this.visited = p.visited;
        this.totalDOMNodes = 0;
        this.region = p.region;
        modules = new HashMap<String, String>();
    }

    /**
        Create a webpage with a url. Member data will be initialized if data files exist.
     */
    public WebPage(String url) {

        this.url = CleanUrl(url); //System.out.println("Cleaned url: " + this.url);
        this.id = GenerateID(this.url); //System.out.println("Generated Id: " + this.id);
        this.lastUpdated = new Date();
        this.visited = false;
        this.totalDOMNodes = 0;
        modules = new HashMap<String, String>();

        this.region = ExtractRegion();
        this.pageSource = new String();
    }

    /******************
        Public Methods
     ******************/

    /**
     * There is a main nav.
     * Search for class="help-link"
     * @return
     */


    /************
        Mutators
     ************/

    public void SetTitle(String title) {
        this.title = title;
    }

    public void SetPageSource(String source) {
        pageSource = source;
    }

    public void SetPageSourcePath(String path) { sourceFilePath = path; }

    public void SetVisited() {
        visited = true;
    }

    public void SetText(String text) { this.text = text; }

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

    public void SetRegion(String region) { this.region = region; }

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

    public String GetText() { return this.text; }

    public boolean GetVisited() {
        return visited;
    }

    public Map<String, String> GetModules() {
        return modules;
    }

    public String GetModule(String key) {
        return modules.get(key);
    }

    public String GetRegion() { return region; }

    public static HashMap<String, String> GetAllRegionCodes() {
        return regions;
    }

    public static TreeSet<String> GetAllRegionCodesSorted() {
        return new TreeSet<>(regions.keySet());
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
        urlid = urlid.replaceAll("(\\.)|(/)|(\\?)|(=)|(&)|(-)|(#)|(%)|(\\+)|(')|(:)","");

        return urlid.toLowerCase();
    }

    /**
        Extract the region of the webpage from the id which is generated from the url.
     */
    private String ExtractRegion() {
        String region;

        // extract the last four characters at the end of the id formed from the URL
        String localizationSuffix = id.substring(id.length()-4);
        if(localizationSuffix.equals("frca"))
            region = "fr-ca";
        else if(localizationSuffix.equals("frbe"))
            region = "fr-be";
        else if(localizationSuffix.equals("frch"))
            region = "fr-ch";
        else if(localizationSuffix.equals("enhk"))
            region = "en-hk";
        else if(id.equals("storegooglecom")) {
            region = "us";
        }
        else {
            // extract the two characters after "storegooglecom"
            String potentialRegion = id.substring(14, 16);
            if(regions.containsKey(potentialRegion) &&
                    !((id.length() > 20 && id.substring(14, 21).equals("product")) ||
                            (id.length() > 21 && id.substring(14,22).equals("category"))))
                region = potentialRegion;
            else
                region = "us"; // the URL is US region if no region code is specified in path
        }

        return region;
    }

    /**
         This function will clean up the id and url (remove host language query if unneccessary). Host language
        query is considered unnecessary if the region pages are only created in one language.
        This function is called in the WebPage constructor.
        Currently it is designed to handle store.google.com domain.

        Example removing unnecessary host language query:
        https://store.google.com/th/?hl=th-TH -> https://store.google.com/th/

     */
    private String CleanUrl(String url) {
        int HLQStrLength = 9; // maximum potential length of host language query string
        String urlSuffix = url.substring(url.length() - HLQStrLength); // no out of bounds error since minimum length is way beyond 9
        if(urlSuffix.contains("hl=") && !urlSuffix.contains("fr-CA") && !urlSuffix.contains("fr-BE") &&
                !urlSuffix.contains("fr-CH") && !urlSuffix.contains("en-HK") && !urlSuffix.contains("fr-ca") && !urlSuffix.contains("fr-be") &&
                !urlSuffix.contains("fr-ch") && !urlSuffix.contains("en-hk")) {

            url = url.substring(0, url.length() - HLQStrLength);
        }

        return url;
    }


}
