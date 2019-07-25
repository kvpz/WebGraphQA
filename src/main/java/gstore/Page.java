package gstore;

import com.wg.MyUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;

/**
 * Composition of a JSoup Document.
 */
public class Page {
    private File file;
    private ArrayList<Module> modules;
    private Document doc;
    private String description;
    private String url;
    private String title;
    private ArrayList<String> imageUrls;

    private Page() {}

    public Page(File htmlFile) {
        file = new File(htmlFile + File.separator + "source.html");
        url = MyUtil.CreateURLFromWebpageDirName(htmlFile.getName());
        modules = new ArrayList<Module>();
        try {
            doc = Jsoup.parse(file, "utf-8");

            initializeModules();
            initializeTitle();
            initializeDescription();

        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Extract modules from html and initialize the member list of modules.
     */
    private void initializeModules() {
        Elements moduleElements = doc.select("div[class*='page-module']");
        modules = new ArrayList<>(moduleElements.size());
        for(int i = 0; i < moduleElements.size(); ++i) {
            // initialize a new module object
            Module currentMod = new Module(moduleElements.get(i), url);
            modules.add(currentMod);
        }
    }

    /**
     * Extract the description from html and initialize member object for description
     */
    private void initializeDescription() {
        Element head = doc.head();
        Elements del = head.getElementsByAttributeValueContaining("name", "description");
        description = del.first().attr("content");
    }

    private void initializeTitle() {
        title = doc.title();
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public String getUrl() {
        return url;
    }

    public File getFile() {
        return file;
    }

    public int moduleCount() {
        return modules.size();
    }

    public String getText() {
        return doc.text();
    }

    /**
     * Get a page module by its id. Note that some modules do not have an id.
     * @param id module identifier
     * @return
     */
    public Module getModule(String id) {
        Module result = null;

        for(Module m : modules) {
            if(m.getId().equals(id)) {
                result = m;
                break;
            }
        }

        return result;
    }

    public ArrayList<String> getImageUrls() {
        if(imageUrls != null && !imageUrls.isEmpty())
            return imageUrls;

        imageUrls = new ArrayList<String>();
        for(Module m : modules) {
            //imageUrls.addAll(m.getImageUrls());
            for(String url : m.getImageUrls()) {
                imageUrls.add(url);
            }
        }

        return imageUrls;
    }


}
