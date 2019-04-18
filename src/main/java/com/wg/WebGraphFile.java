package com.wg;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map;

/**
    This class manages reading and writing Graph data to and from files.

    The file format for a web graph is ParentDir > PageDir > {PageSource, PageDataFile[*]}
    Each page, uniquely determined by its url, will have its own directory. Most of the large objects will be stored
    within its own file such as the page source and member data structures.

    The class will be able to create and initialize webpage objects. The following files represent a Webpage object:
    source.html (page source)
    modules.dat (module ids)
    meta.dat    (other member data that consume only one line in the file)

 */
public class WebGraphFile {

    private static final String WEBGRAPHFILE_PATH = "WebGraph" + File.separator; // the location where scraped data is stored
    private String webPageDirPath;
    private String sourceFilePath;
    private String metaFilePath;
    private File sourceFile;
    private File metaFile;
    private boolean overwrite = false;

    private WebGraphFile() {

    }

    /**
        The initializes the path member data where the webpage data should be.
     */
    public WebGraphFile(WebPage webPage) {
        webPageDirPath =  WEBGRAPHFILE_PATH + webPage.GetId() + File.separator;
        metaFilePath = webPageDirPath + "meta.dat";
        sourceFilePath = webPageDirPath + "source.html";
        System.out.println("WebGraphFile constructor: webPageDirPath: " + webPageDirPath);
    }

    /**
        Returns the webpage dedicated directory file path if it exists, else returns null;
     */
    public String GetWebPageFilePath() {
        File file = new File(webPageDirPath);
        if(file.exists()) {
            System.out.println("File " + webPageDirPath + " exists");
            return file.getAbsolutePath();
        }

        return null;
    }

    public String GetWebPageSourceFilePath() {
        File file = new File(sourceFilePath);
        if(file.exists()) {
            System.out.println("File " + sourceFilePath + " exists");
            return file.getAbsolutePath();
        }

        return null;
    }


    /**
        Persist graph data to a file. This function will overwrite a file if it exists by default.
     */
     public void WriteWebPageToFile(WebPage webPage) {

        try {
            File pageFile = new File(webPageDirPath);
            pageFile.mkdir();
            System.out.println("Directory " + webPageDirPath + " created.");
            FileWriter pageSourceOut = new FileWriter(sourceFilePath);
            FileWriter pageMetaOut = new FileWriter(metaFilePath);

            pageSourceOut.write(webPage.GetPageSource());

            pageMetaOut.write("+ Title\n" + webPage.GetTitle() + "\n");

            // Store module IDs
            pageMetaOut.write("+ Modules\n");
            Map<String, String> modules = webPage.GetModules();
            for (String id : modules.keySet()) {
                pageMetaOut.write(id + "\n");
            }
            pageMetaOut.close();
            pageSourceOut.close();

        }
        catch(Exception e) {
            System.out.println("Exception in WriteWebPageToFile(WebPage)\n" + e);
        }

    }
}
