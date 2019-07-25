package gstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * To make this class flexible and reliable, users will be able to select which types of diffs they want
 * to perform on a pair of pages.
 *
 * Each module that performs a diff should return an array summarizing the results in order to simplify reporting.
 */
public class PageDiff {

    private Page pa;
    private Page pb;

    private int paModuleCount;
    private int pbModuleCount;

    private static String ignoreModuleId = "171900b8ac595c74"; // this module has a nonce which causes false positives

    public PageDiff(Page a, Page b) {
        // verify that the pages are valid.
        if(a == null || b == null) {
            throw new ExceptionInInitializerError("Page objects are null.");
        }

        pa = a;
        pb = b;

        paModuleCount = a.moduleCount();
        pbModuleCount = b.moduleCount();
    }

    /**
     *
     * @return true if the module count differs
     */
    public boolean compareModuleCount() {
        Boolean result = false;
        int sizeDiff = 0;

        if(paModuleCount == 0 || pbModuleCount == 0) {
            paModuleCount = pa.getModules().size();
            pbModuleCount = pb.getModules().size();
        }

        if(paModuleCount != pbModuleCount) {
            result = true;
            return result;
        }

        return result;
    }

    /**
     * Iterate through all the modules
     * @return
     */
    public boolean copyDiff() {
        boolean result = false;

        if(!pa.getText().equals(pb.getText())) {
            result = true;
        }

        return result;
    }

    /**
     * Perform a diff on the HTML of the modules that have the same id
     */
    public void diffModulesWithSameId() {
        ArrayList<String> ids = getModulesWithSameId();

        for(String id : ids) {
            Module ma = pa.getModule(id);
            Module mb = pb.getModule(id);
            boolean areEqual = ma.getHtml().equals(mb.getHtml());
            if(areEqual) {
                System.out.println(id + " is equal for both versions of the page");
            }
            else if(!id.equals(getIgnoreModuleId())){
                System.out.println(id + " is NOT equal for both versions of the page");
            }
        }
    }

    /**
     * Attempt to find a difference between the image URLs found on the page. I'm worried that this
     * test may cause a false negative if a different image is assigned the same URL as before.
     */
    public void diffImageUrls() {

    }

    /**
     * Finds and returns all the module ids shared between two pages. Modules that have ids assigned to them can
     * have a diff test performed on the HTML because, as far as I know, these modules do not contain nonces which
     * can create a false positive.
     * @return
     */
    private ArrayList<String> getModulesWithSameId() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Module> pam = pa.getModules();
        ArrayList<Module> pbm = pb.getModules();

        HashMap<String, DiffMachine.AVAILABILITY> idMap = new HashMap<>();

        for(Module m : pam) {
            if(m.getId().isEmpty()) {
                continue;
            }
            idMap.put(m.getId(), DiffMachine.AVAILABILITY.NEW);
        }

        for(Module m : pbm) {
            if(m.getId().isEmpty()) {
                continue;
            }
            if(idMap.get(m.getId()) != null) {
                idMap.replace(m.getId(), DiffMachine.AVAILABILITY.BOTH);
            }
        }

        for(Map.Entry<String, DiffMachine.AVAILABILITY> entry : idMap.entrySet()) {
            if(entry.getValue().equals(DiffMachine.AVAILABILITY.BOTH)) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    /**
     * id of the module that causes a false positive in diff tests.
     * This is a temporary workaround and should be eliminated.
     * @return
     */
    public static String getIgnoreModuleId() {
        return ignoreModuleId;
    }

    /**
     * A report generated for the tests performed.
     */
    public void report() {
        System.out.println("Page a: " + pa.getUrl());
        System.out.println("Module count: " + paModuleCount);

        System.out.println("\n\t---------------------------------------------------------\n");

        System.out.println("Page b: " + pb.getUrl());
        System.out.println("Module count: " + pbModuleCount);
    }
}
