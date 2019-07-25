package gstore;

import com.wg.MyUtil;
import com.wg.WebGraph;
import com.wg.WebGraphFile;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Find differences between current and older html files of WebGraph directories.
 *
 *
 */
public class DiffMachine {

    public static final String webGraphOldName = "WebGraph_2019_6_18" + File.separator;
    public static final String webGraphNewName = "WebGraph_2019_6_22" + File.separator;
    private static final File webGraphOldFile = new File(webGraphOldName);
    private static final File webGraphNewFile = new File(webGraphNewName);
    public static WebGraph webGraphOld;
    public static WebGraph webGraphNew;
    private static String region;
    enum AVAILABILITY {NEW, OLD, BOTH} // identifier for whether a module occurs in new or old version of page, or both.



    /**
     * Search for a key in a tree set in logarithmic time.
     * @param treeSet
     * @param key
     * @return
     */
    private static Object search(TreeSet treeSet, Object key) {
        Object ceil = treeSet.ceiling(key);
        Object floor = treeSet.floor(key);
        return ceil == floor ? ceil : null;
    }

    /**
     * Perform a full diff across an entire webgraph. This should only be run overnight.
     */
    public static void webGraphDiff() {
        region = "us";
        boolean doPageListSizeDiffer = false;
        boolean isNewPageListSizeBigger = false;
        // Get a list of all pages from the US from the old webGraph
        TreeSet<File> oldPageSet = new TreeSet<>();
        // Get a list of all pages from the US from the new WebGraph
        TreeSet<File> newPageSet = new TreeSet<>();
        // will contain all the urls shared by both pages
        HashMap<File, AVAILABILITY> oldNewPageMap = new HashMap<>();
        HashMap<String, File> fileUrlMap = new HashMap<>(); // key map for URLs to Files

        for(File f : WebGraphFile.GetAllDirsFromRegion(region, webGraphOldFile)) {
            oldPageSet.add(f);
            fileUrlMap.put(MyUtil.CreateURLFromWebpageDirName(f.getName()), f);
        }

        for(File f : WebGraphFile.GetAllDirsFromRegion(region, webGraphNewFile)) {
            newPageSet.add(f);
            fileUrlMap.put(MyUtil.CreateURLFromWebpageDirName(f.getName()), f);
        }

        if(oldPageSet.size() != newPageSet.size()) {
            doPageListSizeDiffer = true;
            System.out.println("List of pages differ. ");
            if(oldPageSet.size() > newPageSet.size()) {
                System.out.println("Old webgraph list has more pages");
            }
            else {
                isNewPageListSizeBigger = true;
                System.out.println("New webgraph has more pages");
            }
        }

        if(doPageListSizeDiffer && isNewPageListSizeBigger) {
            for(File f : oldPageSet) {
                if(newPageSet.contains(new File(webGraphNewName + region + File.separator + f.getName()))) {
                    oldNewPageMap.put(f, AVAILABILITY.BOTH);
                }
                else {
                    oldNewPageMap.put(f, AVAILABILITY.OLD);
                }
            }
        }
        else if(doPageListSizeDiffer) { // implied old webgraph has more files
            for(File f : newPageSet) {
                if(oldPageSet.contains(new File(webGraphOldName + region + File.separator + f.getName()))) {
                    oldNewPageMap.put(f, AVAILABILITY.BOTH);
                }
                else {
                    oldNewPageMap.put(f, AVAILABILITY.NEW);
                }
            }
        }
        else
            System.out.println("WebGraphs do not differ");

        int moduleDifferentialCount = 0;
        // run through the map and perform an in depth diff on the pages found in both webGraphs
        for(File e : oldNewPageMap.keySet()) {
            // perform in-depth diff of pages found in old and new webgraph
            if(oldNewPageMap.get(e) == AVAILABILITY.BOTH) {
                Page oldPage = new Page(new File(webGraphOldName + region + File.separator + e.getName()));
                Page newPage = new Page(new File(webGraphNewName + region + File.separator + e.getName()));
                PageDiff diff = new PageDiff(oldPage, newPage);

                boolean modulesCountDiffers = diff.compareModuleCount();
                if(modulesCountDiffers) {
                    ++moduleDifferentialCount;
                }

                //boolean hasSimilarModuleIds = getSimilarModules(oldPage, newPage);
            }

        }

        System.out.println("Total pages with differing modules: " + moduleDifferentialCount + " out of " + fileUrlMap.size());

    }

    /**
     * Combine files from different webgraph region directories, i.e. files names found in both.
     */
    public static HashMap<File, AVAILABILITY> combineSimilarFiles() {
        HashMap<File, AVAILABILITY> map = new HashMap<>();

        // iterate through old webgraph files
        try {
            WebGraph oldWebGraph = new WebGraph(new File(webGraphOldName));
            for(WebGraphFile wgFile : oldWebGraph.getFiles()) {
                map.put(wgFile.getFile(), AVAILABILITY.OLD);
            }
        }
        catch(InstantiationException e) {
            System.out.println(e);
        }

        // iterate through new webgraph files
        try {
            WebGraph newWebGraph = new WebGraph(new File(webGraphOldName));
            for(WebGraphFile wgFile : newWebGraph.getFiles()) {
                if(map.get(wgFile.getFile()).equals(AVAILABILITY.OLD)) {
                    map.replace(wgFile.getFile(), AVAILABILITY.BOTH);
                }
                else {
                    map.put(wgFile.getFile(), AVAILABILITY.NEW);
                }
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }

        for(Map.Entry<File, AVAILABILITY> entry : map.entrySet()) {
            if(!entry.getValue().equals(AVAILABILITY.BOTH)) {
                map.remove(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    @Test
    public void combineSimilarFilesTest() {
        HashMap<File, AVAILABILITY> map = combineSimilarFiles();
        for(Map.Entry<File, AVAILABILITY> e : map.entrySet()) {

            System.out.println(e.getKey() + " - " + e.getValue());
        }
    }

    public static void main(String... args) {

        // type of diff to perform: d

        // perform a full differential between two webgraphs
        webGraphDiff();


        // compare the copy of two similar (same URL) pages
        //PageDiff pageDiff = new PageDiff();

        // Diffs to perform:
        // two similar URL pages from the same region (same and different dates)
        // two similar URL (product) pages from different region (same & different dates)
        // difference between subnav modules (text & links)
        //

    }
}
