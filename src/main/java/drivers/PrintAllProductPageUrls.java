package drivers;

import com.wg.MyUtil;
import com.wg.WebGraphFile;

import java.io.File;

public class PrintAllProductPageUrls {
    public static void main(String[] args) {
        for(File pageDir : WebGraphFile.GetProductPages()) {
            String url = MyUtil.CreateURLFromWebpageDirName(pageDir.getName());
            System.out.println(url);
        }
    }
}
