package drivers;

import com.wg.WebGraphFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Print out all the pages under the magazine path which list product retailers.  This url
 * can be found on the pdp page attached to the transaction button.
 */
public class MagazineRetailerPages {



    public static void main(String[] args) throws IOException, URISyntaxException {
        HashMap<String, HashSet<String>> map = WebGraphFile.GetConfigUrlFromProductPages();

        for(String region : map.keySet()) {
            if(region.contains("magazine"))
                System.out.println("https://" + region);

        }

        System.out.println("Total transaction button urls: " + map.keySet().size());
    }
}
