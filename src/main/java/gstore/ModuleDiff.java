package gstore;

public class ModuleDiff {
    private Module ma, mb;

    public ModuleDiff(Module a, Module b) {
        if(a == null || b == null) {
            throw new ExceptionInInitializerError("Module objects are null.");
        }

        ma = a;
        mb = b;
    }

    /**
     * returns true if there is a difference between the html of the two modules
     * @return
     */
    public boolean htmlDiff() {
        boolean result = false;
        String maHtml = ma.getHtml();
        String mbHtml = mb.getHtml();

        if(maHtml.compareTo(mbHtml) != 0)
            result = true;

        return result;
    }

    /**
     * Check if there is a difference between the copy on the page.
     * @return
     */
    public boolean copyDiff() {
        boolean result = false;

        String maText = ma.getText();
        String mbText = mb.getText();
        if(maText.compareTo(mbText) != 0)
            result = true;

        return result;
    }
}
