package drivers;

import com.wg.MyUtil;
import com.wg.WebGraphFile;
import gstore.DownloadPages;

import java.io.File;

/**
 * Download the page source for directories that do not have source.html.
 *
 * Currently this implemented to only work on Mac because of the .DS_Store directory.  Hack away to
 * make it compatible with other OSes, too.
 */
public class DownloadMissingSources {

    public static void main(String[] args) {
        // iterate through all page directories across all regions
        for(File region : WebGraphFile.GetAllRegionDirs()) {
            for(File pageDir : WebGraphFile.GetAllDirsFromRegion(region.getName())) {
                // less than one because MAC DS_Store
                if(pageDir.listFiles().length < 1) {
                    DownloadPages.downloadPage(MyUtil.CreateURLFromWebpageDirName(pageDir.getName()),
                            pageDir.getParentFile());
                }
            }

        }
    }
}
