package drivers;

import com.wg.WebGraphFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Print the nav links.
 *
 * Format:
 * REGION LINKNAME URL
 */
public class printNavLinks {

    private static Element getNavElement(File pageDir) {
        Element navElement;
        File pageSource = new File(pageDir + "/source.html");
        Document doc = null;
        try {
            doc = Jsoup.parse(pageSource, "utf-8");
        }
        catch(IOException e) {
            System.out.println(e);
        }

        navElement = doc.getElementsByClass("navbar").first();

        return navElement;
    }

    /**
     * Get the links from the nav
     * @return
     */
    public static HashMap<String, String> getLinks(Element nav) {
        HashMap<String, String> navLinks = new HashMap<>();
        Elements anchors = nav.getElementsByTag("a");
        for(Element a : anchors) {
            String linkName = a.text();
            String href = a.attr("href");
            navLinks.put(linkName, href);
        }
        return navLinks;
    }

    public static HashMap<String, HashMap<String, String>> getLinks() {
        HashMap<String, HashMap<String, String>> navLinksPerRegion = new HashMap<>();
        // iterate through regions
        for(File regiondir : WebGraphFile.GetAllRegionDirs()) {
            // get any page source because nav is global
            File homePageDir = regiondir.listFiles()[0];

            // get the nav element
            Element nav = getNavElement(homePageDir);

            // get links from nav element
            HashMap<String, String> navLinks = getLinks(nav);

            navLinksPerRegion.put(regiondir.getName(), navLinks);
        }
        // get links from nav elements

        return navLinksPerRegion;
    }

    public static void main(String[] args) {
        HashMap<String, HashMap<String, String>> regionalNavLinks = getLinks();
        for(Map.Entry<String, HashMap<String, String>> re : regionalNavLinks.entrySet()) {
            for(Map.Entry<String, String> link : re.getValue().entrySet()) {
                System.out.println(re.getKey() + " " + link.getKey() + " " + link.getValue());
            }
        }
    }
}
