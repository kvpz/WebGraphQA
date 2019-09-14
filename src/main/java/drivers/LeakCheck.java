package drivers;

import com.wg.WebGraphFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;

/**
 * Check all the pages for a mention of codenames or names of products that have not been released.
 */
public class LeakCheck {

    public static Document getDocument(File pageDir) {
        Document doc = Document.createShell(pageDir.toURI().toString());
        File pageSource = new File(pageDir + File.separator + "source.html");
        try {
            doc = Jsoup.parse(pageSource, "utf-8");
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return doc;
    }

    public static void main(String[] args) {
        // store all codenames from file into memory


        // store all unrevealed product names into memory


        // iterate through all the regions
        for(File region : WebGraphFile.GetAllRegionDirs()) {
            for(File pageDir : WebGraphFile.GetAllDirsFromRegion(region.getName())) {
                Document doc = getDocument(pageDir);

                if(doc.html().contains("Nest Wifi")) {
                    System.out.println(doc.title());
                }
            }
        }

        // iterate through all the pages for a region
        // search for the words in the region by iterating through all the words and performing a search on html
        // if a word is found, print the page and the word that was found in it

    }
}
