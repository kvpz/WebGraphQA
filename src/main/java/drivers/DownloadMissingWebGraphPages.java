package drivers;

import com.wg.WebPage;
import gstore.DownloadPages;
import gstore.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Download pages that are not in the webgraph.  The URLs for the pages should be read from a file.
 */
public class DownloadMissingWebGraphPages {

    private static final File webGraph = new File("WebGraph");

    public static void main(String[] args) throws IOException {
        // iterate through all urls from urls_<date>

        File urlsFile = new File("urls_" + Utility.dateUnderscored());

        Path urlsFilePath = Paths.get(urlsFile.toURI());
        List<String> urls = Files.readAllLines(urlsFilePath);
        Collections.sort(urls);
        for(String url : urls) {
            WebPage page = new WebPage(url);
            File regionDir = new File(webGraph + File.separator + page.GetRegion());
            DownloadPages.downloadPage(url, regionDir);
        }

    }
}
