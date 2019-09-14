package drivers;

import com.wg.MyUtil;
import com.wg.WebGraphFile;
import com.wg.WebPage;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Find and print all the config URLs.
 *
 */
public class PrintAllConfigUrls {

    public static void main(String[] args) throws Exception {
        MyUtil.Timer mytimer = new MyUtil.Timer();

        HashSet<String> configUrls = new HashSet<>();

        // get the config URLs found in the product pages
        HashMap<String, HashSet<String>> map = WebGraphFile.GetConfigUrlFromProductPages();
        for(String key : map.keySet()) {
            configUrls.add("https://" + key);
        }

        // get config URLs found assigned to href attributes across the website
        for(String region : WebPage.GetAllRegionCodesSorted()) {
            HashMap<String, HashSet<String>> hrefMap = WebGraphFile.GetAllHrefValuesFromLocalRegionSources(region);
            for(String key : hrefMap.keySet()) {
                String url = "https://store.google.com/" + region + key;
                if(url.contains("config"))
                    configUrls.add(url);

                // where the URL can be found
                /*
                for(String value : hrefMap.get(key)) {
                    System.out.println("value: " + value);
                }
                */
            }
        }

        System.out.println("Config URLs found across webgraph");
        System.out.println("Total config urls found: " + configUrls.size());
        for(String url : configUrls) {
            System.out.println(url);
        }

        System.out.println("Total time processing: ");
        mytimer.End();
    }
}
