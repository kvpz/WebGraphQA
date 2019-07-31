package gstore;

import com.wg.MyUtil;
import com.wg.WebGraphFile;
import com.wg.WebPage;
import org.apache.commons.cli.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Default usage of this program will download all the pages currently in the WebGraph directory.  The WebGraph
 * directory contains almost all pages that have ever been encountered in GStore since Spring season.  Because of
 * that, there are requests for pages that will receive a redirect response.
 *
 * Download pages from a region
 * Download pages into an already existing WebGraph directory (overwriting)
 *
 * Future plans:
 * Add a flag that for preventing the download of pages that are currently deprecated, i.e. URLs requests that will
 * receive a 301 response.  This will help speed up tests.
 */
public final class DownloadPages {

    private static final String DOMAIN = "https://store.google.com";
    private static final String latestWebGraphDir = "WebGraph_" + Utility.dateUnderscored();
    private static Options cliOptions;
    private static boolean verbose = false;

    // CLI flag argument values
    static String region;
    static String overwrite;
    static String downloadUrl;
    static String batchDownload;

    static CommandLineParser cliParser = new DefaultParser();
    static HelpFormatter cliFormatter = new HelpFormatter();
    static CommandLine cmd = null;

    /**
     * Download a page if it does not already exist.
     * @param url page to download
     * @param webGraphRegionDir region directory where page will be stored
     */
    public static void downloadPage(String url, File webGraphRegionDir) {
        WebPage webPage = new WebPage(url);
        webPage.SetPageSourcePath(webGraphRegionDir + File.separator + webPage.GetId() + File.separator + "source.html");

        // if source is already downloaded, skip
        if(Files.exists((new File(webPage.GetPageSourcePath()).toPath()))) {
            System.out.println(webPage.GetPageSourcePath() + " already exists. Not downloading.");
            return;
        }

        WebDriver wd = MyUtil.CreateFFDriver();
        wd.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        wd.get(url);
        MyUtil.Sleep(1750);

        if(!wd.getCurrentUrl().equals(url)) {
            System.out.println("A redirect occurred from " + url + " to " + wd.getCurrentUrl());
            wd.quit();
            return;
        }

        webPage.SetPageSource(wd.getPageSource());
        WebGraphFile webGraphFile = new WebGraphFile();
        webGraphFile.WriteWebPageToFile(webPage, webGraphRegionDir);

        wd.quit();
    }

    private static void downloadPageOverwrite(String url, File webGraphRegionDir) {
        WebPage webPage = new WebPage(url);
        webPage.SetPageSourcePath(webGraphRegionDir + File.separator + webPage.GetId() + File.separator + "source.html");

        WebDriver wd = MyUtil.CreateFFDriver();
        //wd.manage().timeouts().pageLoadTimeout(8, TimeUnit.SECONDS);
        wd.get(url);
        MyUtil.Sleep(1750);
        if(!wd.getCurrentUrl().equals(url)) {
            System.out.println("A redirect occurred from " + url + " to " + wd.getCurrentUrl());
            wd.quit();
            return;
        }

        webPage.SetPageSource(wd.getPageSource());
        WebGraphFile webGraphFile = new WebGraphFile();
        webGraphFile.WriteWebPageToFile(webPage, webGraphRegionDir);

        wd.quit();
    }

    /**
     * Download all the pages for a current region from previous WebGraph directory
     * @param region
     */
    private static void downloadFromRegion(String region, File webGraphRegionDir) {
        ArrayList<File> webGraphRegion = WebGraphFile.GetAllDirsFromRegion(region);
        for(File f : webGraphRegion) {
            // create url from file name
            String url = MyUtil.CreateURLFromWebpageDirName(f.getName());
            if(url == null) {
                System.out.println("Issue creating URL from file " + f.getName());
                System.exit(1);
            }

            // download page
            downloadPage(url, webGraphRegionDir);
        }
    }

    /**
     * Verify if the region input is valid or not
     * @param region
     */
    private static boolean verifyRegionArgVal(String region) {
        TreeSet<String> validRegions = WebPage.GetAllRegionCodesSorted();

        if(!validRegions.contains(region)) {
            System.out.println("The region code is invalid.");
            System.out.println("Region code must be one of the following:");
            for(String validRegion : validRegions) {
                System.out.println(validRegion);
            }

            return false;
        }

        return true;
    }

    private static boolean verifyDownloadURLArgVal(String url) {
        if(!url.contains(DOMAIN)) {
            System.out.println(url + " is not valid.");
            return false;
        }

        return true;
    }

    /**
     * Check if the flags provided via CLI are valid.  If they are not, the program should be aborted.
     * @return true if flag values are valid, else false
     */
    private static void verifyCLIFlags() {
        // check if region flag value is valid
        if(region != null && !verifyRegionArgVal(region)) {
            System.exit(0);
        }

        // check if url flag value is valid
        if(downloadUrl != null && !verifyDownloadURLArgVal(downloadUrl)) {
            System.exit(0);
        }
    }

    /**
     * Create all the flags that will be accepted by the program via CLI.
     */
    private static void setupCLIOptions() {
        cliOptions = new Options();

        Option overwriteFlag = new Option("o","overwrite", false, "overwrite stored html (default)");
        overwriteFlag.setRequired(false);
        cliOptions.addOption(overwriteFlag);

        Option regionFlag = new Option("r", "region", true, "download pages from this region");
        regionFlag.setRequired(false);
        cliOptions.addOption(regionFlag);

        Option urlFlag = new Option("u", "url", true, "page to download");
        urlFlag.setRequired(false);
        cliOptions.addOption(urlFlag);

        Option batchFlag = new Option("b", "batchDownloadList", true, "a file containing all the URLs to be " +
                "downloaded");
        batchFlag.setRequired(false);
        cliOptions.addOption(batchFlag);

        // TODO: create a flag containing the directory where all the pages will be downloaded to instead of the latest
    }

    /**
     * Parse cli options and store flag values as member data.
     * @param args
     */
    private static void parseCLIOptions(String[] args) {
        try {
            cmd = cliParser.parse(cliOptions, args);
        }
        catch(Exception e) {
            System.out.println(e);
            cliFormatter.printHelp("utility-name", cliOptions);

            System.exit(1);
        }

        // get cli flag values
        overwrite = cmd.getOptionValue("overwrite");
        region = cmd.getOptionValue("region");
        downloadUrl = cmd.getOptionValue("url");
        batchDownload = cmd.getOptionValue("batchDownloadList");
    }

    public static void main(String... args) {

        setupCLIOptions();
        parseCLIOptions(args);

        // Perform checks that should abort the program
        verifyCLIFlags();

        // do stuff if only region argument is passed (download all pages from previous webgraph)
        if(region != null && downloadUrl == null) {

            // get the latest WebGraph directory
            File webGraphDir = new File("WebGraph_2019_7_30"); //latestWebGraphDir);
            boolean created = webGraphDir.mkdir();
            if(!created) {
               System.out.println(webGraphDir.getName() + " was not created (likely because it already exists)");
            }

            // create a directory under the WebGraph directory for the region
            File webGraphRegionDir = new File(webGraphDir.getName() + File.separator + region);
            boolean regionDirCreated = webGraphRegionDir.mkdir();
            if(!regionDirCreated) {
                System.out.println(webGraphRegionDir + " was not created (likely because it already exists)");
            }

            // prevent writing logs to console
            System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
            System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
                    "geckologs_" + Utility.dateUnderscored() + "_" + region + ".txt");

            downloadFromRegion(region, webGraphRegionDir);
        }

        // download the page (only for US)
        if(downloadUrl != null && region == null) {
            downloadPageOverwrite(downloadUrl, new File(latestWebGraphDir + File.separator + "us"));
        }

    }
}
