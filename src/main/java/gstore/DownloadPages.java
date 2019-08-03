package gstore;

import com.wg.MyUtil;
import com.wg.WebGraphFile;
import com.wg.WebPage;
import org.apache.commons.cli.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
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
    static String batchDownload; // name of the file with all the URLs to download
    static String webGraphDirName;

    static CommandLineParser cliParser = new DefaultParser();
    static HelpFormatter cliFormatter = new HelpFormatter();
    static CommandLine cmd = null;

    private static ArrayList<String> getFileLines(File file) {
        try {
            ArrayList<String> lines = (ArrayList<String>)Files.readAllLines(file.toPath());
            return lines;
        }
        catch(IOException e) {
            System.out.println("There was an error in getFileLines");
            System.out.println(e.getMessage());
        }

        return new ArrayList<String>();
    }

    /**
     * Download all the pages for a region listed in a file of URLs.  If the WebGraph/Region directory does not
     * exist, it will be created.  By default, the file will not be redownloaded if it already exists in the directory.
     */
    private static void batchDownload() {
        // Get the webGraph directory where the files will be downloaded to
        File webGraphDir = getWebGraphDir(webGraphDirName); // directory existence verified
        // Get the WebGraph REGION directory where tie files will be downloaded to
        File webGraphRegionDir = getWebGraphRegionDir(webGraphDir);

        // Get and/ or open the region directory under the specified WebGraph directory

        // process the list of URLs
        File urlList = new File(batchDownload);
        for(String url : getFileLines(urlList)) {
            // download the page and store it in the region directory under the requested webgraph
            downloadPage(url, webGraphRegionDir);
        }
    }

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

        // this option will require that the WebGraph be stored locally
        Option webGraphFlag = new Option("w", "webgraph", true, "an existing relative WebGraph directory");
        webGraphFlag.setRequired(false);
        cliOptions.addOption(webGraphFlag);

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
        webGraphDirName = cmd.getOptionValue("webgraph");
    }

    /**
     * Verify that webgraph directory is stored in the same directory as this program
     * @param webGraphDir
     * @return
     */
    private static boolean verifyWebGraphDir(String webGraphDir) {
        boolean result = false;
        File dir = new File(webGraphDir);
        if(dir.exists()) {
            result = true;
        }
        else {
            System.out.println("The WebGraph specified in the CLI is not available");
        }

        return result;
    }

    /**
     * Verify that the set of flags provided via CLI are valid.
     * Invalid case example: downloadUrl and batchDownload flags are both declared
     * @return true if the flag combination is valid
     */
    private static boolean validFlagCombinations() {
        boolean result = true;

        if(downloadUrl != null && batchDownload != null) {
            result = false;
            System.out.println("Both flags downloadUrl and batchDownload cannot be declared together.");
        }

        return result;
    }

    /**
     * Check if the flags provided via CLI are valid.  If they are not valid, the program should be aborted.
     * The combination of flags should also be valid.
     * @return true if flag values are valid, else false
     */
    private static void verifyCLIFlags() {
        // check if region flag value is valid
        if(region != null && !verifyRegionArgVal(region)) {
            System.exit(1);
        }

        // check if url flag value is valid
        if(downloadUrl != null && !verifyDownloadURLArgVal(downloadUrl)) {
            System.exit(1);
        }

        // Check if the webGraph name refers to an existing file stored relatively
        if(webGraphDirName != null && !verifyWebGraphDir(webGraphDirName)) {
            System.exit(1);
        }

        if(!validFlagCombinations()) {
            System.exit(1);
        }
    }

    /**
     * Get the latest WebGraph directory; create if it does not exist.
     * @return
     */
    private static File getLatestWebGraphDir() {
        File webGraphDir = new File("WebGraph_2019_7_30"); //latestWebGraphDir);
        boolean created = webGraphDir.mkdir();
        if(!created) {
            System.out.println(webGraphDir.getName() + " was not created (likely because it already exists)");
        }

        return webGraphDir;
    }

    /**
     * create/ get a directory under the WebGraph directory for the region
     * @return
     */
    private static File getWebGraphRegionDir(File webGraphDir) {
        File webGraphRegionDir = new File(webGraphDir.getName() + File.separator + region);
        boolean regionDirCreated = webGraphRegionDir.mkdir();
        if(!regionDirCreated) {
            System.out.println(webGraphRegionDir + " was not created (likely because it already exists)");
        }

        return webGraphRegionDir;
    }

    /**
     * Get the WebGraph specified via the command line option.  If it does not exist, the program should terminate.
     * @param webGraphName
     * @return
     */
    private static File getWebGraphDir(String webGraphName) {
        File webGraphDir = new File(webGraphName);

        return webGraphDir;
    }

    public static void main(String... args) {

        setupCLIOptions();
        parseCLIOptions(args);
        verifyCLIFlags();
        System.out.println("Batch download file: " + batchDownload);

        // download pages for a region
        if(region != null && downloadUrl == null && batchDownload == null) {

            // get the latest WebGraph directory
            File webGraphDir = getLatestWebGraphDir();

            // create/ get a directory under the WebGraph directory for the region
            File webGraphRegionDir = getWebGraphRegionDir(webGraphDir);

            // prevent writing logs to console
            System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
            System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
                    "geckologs_" + Utility.dateUnderscored() + "_" + region + ".txt");

            downloadFromRegion(region, webGraphRegionDir);
        }

        // download the page (only for US) and store in latest WebGraph
        else if(downloadUrl != null && region == null && batchDownload == null) {
            downloadPageOverwrite(downloadUrl, new File(latestWebGraphDir + File.separator + "us"));
        }

        // download pages from a list to a specific WebGraph region directory
        else if(region != null && downloadUrl == null && batchDownload != null && webGraphDirName != null) {
            batchDownload();
        }

    }
}
