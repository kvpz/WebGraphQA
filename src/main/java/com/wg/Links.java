package com.wg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Links {

    /**
     * es: look for void, /no, /fr
     */
    public static void GetAllHrefValuesFromLocalRegionSources() {
        Timer timer = new Timer();

        Set<String> regionCodes = WebPage.GetAllRegionCodes().keySet();

        HashMap<String, HashSet<String>> map = new HashMap<>();
        for(String region : regionCodes) {
            //if(!region.equals("pr")) continue;
            System.out.println("Current region: " + region);
            try {
                map = GetAllHrefValuesFromLocalRegionSources(region);
                for(String key : map.keySet()) {
                    if(!key.contains("void")) continue;
                    //if(!key.contains("item"))
                    //    continue;
                    System.out.println("Link: "+ key);

                    System.out.println("Pages link is found in: " );
                    for(String v : map.get(key)) {
                        //if(v.contains("/product/nest_protect")) // && v.length() < )
                        System.out.println(v);
                    }
                    //System.out.println();
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }



        timer.End();

    }

    /**
     Get the value of all href attributes contained within the local page sources files.
     The return value will be a map with an href value as key, and a value of a set of pages which contain
     that URI.
     */
    public static HashMap<String, HashSet<String>> GetAllHrefValuesFromLocalRegionSources(String region) throws IOException {
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();

        ArrayList<File> regions = WebGraphFile.GetAllRegionDirs();

        for (File dir : regions) {
            File[] regionPages = dir.listFiles();
            if(!dir.getName().equals(region)) continue;
            for (File page : regionPages) {
                // remove DS_Store from Mac directories
                if (page.getName().contains("DS_Store"))
                    continue;

                Document html = Jsoup.parse(new File(page + "/source.html"), "utf-8");
                Elements hrefs = html.getElementsByAttribute("href");

                // iterate through pages href tags
                for (Element href : hrefs) {
                    String hrefValue = href.attr("href");


                    if (!map.containsKey(hrefValue)) {
                        HashSet<String> newPageSet = new HashSet<>();
                        map.put(hrefValue, newPageSet);
                        map.get(hrefValue).add(page.getName());
                    } else {
                        map.get(hrefValue).add(page.getName());
                    }


                }
            }
        }
        return map;
    }

    /**
     * go.co is invalid and should be g.co
     * valid urls not hyperlinked: g.co, nest.com, madefor.google
     * @param doc
     * @return
     */
    public static boolean FindUrls(Document doc, HashSet<String> hashSet) {
        boolean hyperlinksMissing = false;
        String text = doc.text();

        String[] words = text.split(" ");
        for(String word : words) {

            if(!word.contains("example") && (word.contains("g.co") || word.contains("go.co") ||
                    word.contains("nest.com") || word.contains("madefor") || word.contains(".com") ||
                    word.contains(".co"))) {
                hyperlinksMissing = true;
                hashSet.add(word);
            }

        }

        return hyperlinksMissing;
    }

    /**
     * Get all the URLs that are not hyperlinked. Returns a map with keys
     * @return
     */
    public static HashMap< String, HashSet<String> > FindUrls() {
        HashMap< String, HashSet<String> > uniqueUrlSet = new HashMap< String, HashSet<String> >();

        for(String region : WebPage.GetAllRegionCodesSorted()) {

            for (File f : WebGraphFile.GetAllDirsFromRegion(region)) {
                String url = MyUtil.CreateURLFromWebpageDirName(f.getName());
                uniqueUrlSet.put(url, new HashSet<>());

                try {
                    boolean isMissingHyperlinks = FindUrls(Jsoup.parse(new File(f + "/source.html"), "utf-8"), uniqueUrlSet.get(url));
                    if (isMissingHyperlinks) {
                        //System.out.println(url);
                    }
                    else
                        uniqueUrlSet.remove(url);

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        }

        return uniqueUrlSet;

    }

    public static void PrintPagesWithMissingHyperlinks(HashMap< String, HashSet<String> > pages) {
        for(String page : pages.keySet()) {
            System.out.println("===== " + page + " =====");
            for(String url : pages.get(page)) {
                System.out.println(url);
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {

        GetAllHrefValuesFromLocalRegionSources();
    }
}
