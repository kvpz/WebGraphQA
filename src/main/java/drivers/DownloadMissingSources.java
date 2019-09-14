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
        // iterate through all region directories
        for(File region : WebGraphFile.GetAllRegionDirs()) {
            // iterate through all page directories in the region
            for(File pageDir : WebGraphFile.GetAllDirsFromRegion(region.getName())) {
                // determine if directory is missing source.html
                File[] files = pageDir.listFiles();
                if(files.length < 1) {
                    DownloadPages.downloadPage(MyUtil.CreateURLFromWebpageDirName(pageDir.getName()),
                            pageDir.getParentFile());
                }
            }

        }
    }
}
