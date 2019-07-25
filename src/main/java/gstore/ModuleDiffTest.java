package gstore;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Class for performing sanity tests
 */
public class ModuleDiffTest {

    Page pa = new Page(new File("WebGraph_2019_6_22/us/storegooglecom"));
    Page pb = new Page(new File("WebGraph_2019_7_24/us/storegooglecom"));


    @Test
    public void htmlDiffSimilarModules() {
        Module a = pa.getModules().get(2);
        Module b = pb.getModules().get(2);

        ModuleDiff mdiff = new ModuleDiff(a, b);

        // a false assertion means that there is no difference in the html
        Assert.assertFalse("error", mdiff.htmlDiff());

    }

    @Test
    public void copyDiffTest() {
        Module a = pa.getModules().get(0);
        Module b = pb.getModules().get(0);

        ModuleDiff mdiff = new ModuleDiff(a,b);
        Assert.assertFalse(mdiff.copyDiff());
    }
}
