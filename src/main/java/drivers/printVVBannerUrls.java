package drivers;

import com.wg.MyUtil;
import com.wg.WebGraphFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashSet;

/**
 * Print all the URLs of pages that have the VV Banner.  This banner only appears in the US and in product pages.
 */
public class printVVBannerUrls {
    public static void main(String[] args) {
        // create a set for storing URLs
        HashSet<String> urls = new HashSet<>();

        // iterate through all page directories in the US
        for(File pageDir : WebGraphFile.GetAllDirsFromRegion("us")) {
            File pageSource = new File(pageDir + File.separator + "source.html");
            try {
                // Initiate a JSOUP object with the page's source
                Document doc = Jsoup.parse(pageSource, "utf-8");

                // Find the banner using an identifier
                Elements bannerEl = doc.getElementsByAttributeValueContaining("data-name", "VV Banner WMBT");
                if(bannerEl.size() > 1) {
                    System.out.println("Why is there more than one banner on one page?");
                    System.out.println(MyUtil.CreateURLFromWebpageDirName(pageDir.getName()));
                }
                else if (bannerEl.size() == 1){
                    // Add the page to a set of URLs
                    urls.add(MyUtil.CreateURLFromWebpageDirName(pageDir.getName()));
                }
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }

        // print the URLs
        for(String url : urls) {
            System.out.println(url);
        }
    }
}
