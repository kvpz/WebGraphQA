package gstore;

import com.google.common.collect.ArrayListMultimap;
import com.wg.MyUtil;
import com.wg.WebGraph;
import com.wg.WebGraphFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageDiffTest {

    @Test
    public void initializationWithNullPage() {
        PageDiff pageDiff = new PageDiff(null, null);

    }

    /**
     * This should warrant a test on the page because it means new content was added. A new test
     * will have to be written to test that new module (if it needs one).
     */
    @Test
    public void compareModuleCount() {
        Page a = new Page(new File("WebGraph/jp/storegooglecomjp"));
        Page b = new Page(new File("WebGraph_2019_6_22/us/storegooglecom"));

        PageDiff diff = new PageDiff(a, b);

        // assert that there is a difference
        Assert.assertTrue(diff.compareModuleCount());
    }

    /**
     * Perform a diff on the html of the modules that that have the same id attribute value.
     * The pages used for this test should not show any difference in the html of the modules.  This is to prove
     * that modules with the same id do not exhibit any difference, i.e. do not contain nonces. This is a sanity test!
     */
    @Test
    public void diffModulesWithSameIds() {
        // if modules have an id value, compare them
        Page a = new Page(new File("WebGraph_2019_7_23/us/storegooglecomusproductpixel_3"));
        Page b = new Page(new File("WebGraph_2019_6_22/us/storegooglecomusproductpixel_3"));

        PageDiff diff = new PageDiff(a, b);

        diff.diffModulesWithSameId();

    }

    @Test
    public void diffOffersPage() {
        Page pNew = new Page(new File("WebGraph_2019_7_24/us/storegooglecomuscollectionoffers"));
        Page pOld = new Page(new File("WebGraph_2019_7_23/us/storegooglecomuscollectionoffers"));

        PageDiff diff = new PageDiff(pNew, pOld);
        if(diff.compareModuleCount()) {
            System.out.println("module count differs");
            //Assert.assertTrue("module count differs", diff.compareModuleCount());
        }

        diff.diffModulesWithSameId();
    }

    @Test
    public void CopyDiffTest() throws Exception {
        WebGraph wgOld = new WebGraph(new File("WebGraph_2019_6_18"));
        WebGraph wgNew = new WebGraph(new File("WebGraph_2019_6_22"));

    }

    /**
     * Perform a diff on the copy of the webpages found in one entire webgraph.
     *
     * Remember that webgraphs may not contain the same amount of files.  This function will only
     * perform a diff on the files found in both webgraphs.
     */
    @Test
    public void copyDiffWebGraph() throws IOException {
        File resultsFile = new File("TestResults/CopyDiffWebGraph_" + Utility.dateUnderscored());
        FileWriter fwriter = new FileWriter(resultsFile);

        WebGraph wgOld = null;
        WebGraph wgNew = null;

        try {
            wgOld = new WebGraph(new File("WebGraph_2019_7_23"));
            wgNew = new WebGraph(new File("WebGraph_2019_7_24"));
        }
        catch(InstantiationException e) {
            System.out.println(e);
            System.out.println("Couldn't instantiate WebGraph");
            System.exit(1);
        }

        ArrayList<WebGraphFile> wgOldFiles = wgOld.getFiles();
        ArrayList<WebGraphFile> wgNewFiles = wgNew.getFiles();

        // get all the pages both WebGraphs have in common
        ArrayListMultimap<String, File> map = ArrayListMultimap.create();

        for(WebGraphFile wgFile : wgOldFiles) {
            map.put(wgFile.getFile().getName(), wgFile.getFile());
        }

        for(WebGraphFile wgFile : wgNewFiles) {
            map.put(wgFile.getFile().getName(), wgFile.getFile());
        }

        for(String key : map.keySet()) {
            List<File> keyValues = null;
            if(map.get(key).size() > 1) {
                keyValues = map.get(key);
                Page pOld = new Page(keyValues.get(0));
                Page pNew = new Page(keyValues.get(1));
                PageDiff pDiff = new PageDiff(pOld, pNew);
                boolean copyDiffResult = pDiff.copyDiff();
                if(copyDiffResult) {
                    fwriter.write(MyUtil.CreateURLFromWebpageDirName(key) + " 1\n");
                }
                else {
                    fwriter.write(MyUtil.CreateURLFromWebpageDirName(key) + " 0\n");
                }
            }
        }

        fwriter.close();
    }


}
