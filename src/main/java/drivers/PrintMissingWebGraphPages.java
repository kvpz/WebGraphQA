package drivers;

import com.wg.MyUtil;
import com.wg.WebGraphFile;
import gstore.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Print and write to file all the URLs found across the website.  The urls written to file
 * can then be used by another program as a list of pages that need to be downloaded.
 */
public class PrintMissingWebGraphPages {

    public static void writeToFileDirNames(HashSet<String> dirNames) throws IOException {
        File file = new File("fileDirNames_" + Utility.dateUnderscored());
        FileWriter fileWriter = new FileWriter(file);

        for(String dirName : dirNames) {
            fileWriter.write(dirName + "\n");
        }

        fileWriter.close();
    }

    public static void writeToFileUrls(HashSet<String> urls) throws IOException {
        File file = new File("urls_" + Utility.dateUnderscored());
        FileWriter fileWriter = new FileWriter(file);

        for(String url : urls) {
            fileWriter.write(url + "\n");
        }

        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        HashSet<String> dirNames = new HashSet<String>();
        HashSet<String> urls = new HashSet<String>();

        // iterate through all the region directories
        for(File regionf : WebGraphFile.GetAllRegionDirs()) {
            HashMap<String, HashSet<String>> page2url =
                    WebGraphFile.GetAllHrefValuesFromLocalRegionSources(regionf.getName());
            // iterate through all the pages
            for(String page : page2url.keySet()) {
                // iterate through all the URLs found on the page (urls formatted as webgraph directory names)
                for(String urlDir : page2url.get(page)) {
                    String url = MyUtil.CreateURLFromWebpageDirName(urlDir);
                    System.out.println(url);
                    urls.add(url);
                    dirNames.add(urlDir);

                }
            }


        }

        writeToFileDirNames(dirNames);
        writeToFileUrls(urls);
    }

}
