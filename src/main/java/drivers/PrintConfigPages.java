package drivers;

import gstore.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Print the URLs of config pages found in the config URLs file.
 */
public class PrintConfigPages {
    // extract all config pages from urls_<date> file


    public static void main(String[] args) throws IOException {
        File urlsFile = new File("urls_" + Utility.dateUnderscored());

        Path urlsFilePath = Paths.get(urlsFile.toURI());
        List<String> urls = Files.readAllLines(urlsFilePath);
        Collections.sort(urls);
        for(String url : urls) {
            if(url.contains("config")) {
                System.out.println(url);
            }
        }
    }

}
