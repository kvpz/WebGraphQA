package com.wg;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * A webgraph is synonymous with a website's sitemap.  A webgraph is simply a directory with all the pages of a website.
 * This directory should contain all pages from a website downloaded on the same day for version control purposes.  A
 * webgraph cannot be instantiated without a webgraph directory.
 *
 * The behavior as of yet does not represent that of a Graph data structure, but that is the end goal.
 *
 * Why does this WebGraph not have a function to return Page objects? I decided to only handle Files through the
 * abstraction provided by WebGraphFile because this will reduce the amount of files that a client will have to use.
 * This library is intended to be light and as flexible as possible.  If a client wants to use another type of Page
 * class than the one I provide, all they will have to do is make calls to this class to get the data that will fulfill
 * their own page objects.  This webgraph may one day server as an abstraction to an entire database.
 *
 */
public class WebGraph {

    private static File WGFILE;

    // Miscellaneous variables
    public static boolean verbose = false;

    private ArrayList<WebGraphFile> webGraphFiles;

    /**
     * A webgraph cannot be instantiated without a webgraph directory existing.
     */
    private WebGraph() {}

    /**
     * Instantiate the WebGraph with a WebGraph directory assumed to be in the correct format.
     * @param dir location of an existing WebGraph directory
     */
    public WebGraph(File dir) throws InstantiationException {
        if(!dir.exists()) {
            if(verbose)
                System.out.println("The directory " + dir + " does not exist. Exiting");
            throw new InstantiationException("The directory " + dir + " + does not exist");
        }

        WGFILE = dir;
        webGraphFiles = new ArrayList<WebGraphFile>();

        // note: path to a directory page: <WebGraph> / <Region> / <Page>
        for(File regionFile : WGFILE.listFiles()) {
            for(File pageFile : regionFile.listFiles()) {
                webGraphFiles.add(new WebGraphFile(this, pageFile));
                if(verbose) {
                    System.out.println("Storing into memory " + pageFile + " from " + WGFILE);
                }
            }
        }
    }

    /**
     * Returns a list of WebGraphFile objects
     * @return
     */
    public ArrayList<WebGraphFile> getFiles() {
        return webGraphFiles;
    }

    /**
     * Set the WebGraph objects to be verbose when operations are performed by it.
     * @param val
     */
    public static void verbose(boolean val) {
        verbose = val;
    }

    public File getWebGraphFile() {
        return WGFILE;
    }
}
