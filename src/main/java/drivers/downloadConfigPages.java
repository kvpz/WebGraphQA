package drivers;

import com.wg.WebPage;
import gstore.DownloadPages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Download all the config pages.
 *
 * The URLs for the config pages are to be extracted from the PDP and used to download the page.  The URLs
 * will be stored in a file which this program will read from.
 */
public class downloadConfigPages {
    private static final File webGraph = new File("WebGraph");

    public static void main(String[] args) throws IOException {
        File configUrlFile = new File("magazineRetailers");

        Path urlsFilePath = Paths.get(configUrlFile.toURI());
        List<String> urls = Files.readAllLines(urlsFilePath);
        Collections.sort(urls);
        for(String url : urls) {
            WebPage page = new WebPage(url);
            File regionDir = new File(webGraph + File.separator + page.GetRegion());
            DownloadPages.downloadPage(url, regionDir);
        }
    }
}
