package gstore;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Module implements Comparator<Module>, Comparable<Module> {
    private String id;
    private List<String> imageUrls;
    private String text;
    private String dataTitle;
    private Element element;
    private boolean hasVideo;
    private boolean hasCarousel; // or slideshow
    private String parentPageUrl; // page that it belongs to
    //private String twitterImageUrl;  // the image shown on twitter share cards
    //private String twitterImageAlt;  // the alt text displayed if image unavailable

    public Module(String id) {
        this.id = id;
    }

    public Module(Element element, String pageUrl) {
        this.element = element;

        parentPageUrl = pageUrl;

        id = this.element.id();

        text = this.element.text();

        dataTitle = this.element.child(0).attr("data-name");

        if(this.element.getElementsByTag("video").size() > 0) {
            hasVideo = true;
        }

        initializeImageUrls();
    }

    /**
     * Get all the URLs pointing to images.
     * Image types: png, jpeg, webm
     *
     */
    private void initializeImageUrls() {
        imageUrls = new ArrayList<>();
        Elements imgElements = element.getElementsByTag("img");
        for(Element imgElement : imgElements) {
            String url = imgElement.attr("src");
            if(url.substring(0,1).equals("//")) {
                url = "https:" + url;
            }
            imageUrls.add(url);
        }

        Elements elsWithStyleAttr = element.getElementsByAttribute("style");
        for(Element elWithStyl : elsWithStyleAttr) {
            String styleVal = elWithStyl.attr("style");
            if(!styleVal.contains("url"))
                continue;

            String url = styleVal.substring( styleVal.indexOf("http"), styleVal.indexOf("\")") );
            System.out.println("url in style:  " + url);
            imageUrls.add(url);
        }

        Elements elsLazyImage = element.getElementsByAttribute("mqn-lazyimage-background-src");
        for(Element el : elsLazyImage) {
            String url = el.attr("mqn-lazyimage-background-src");
            if(url.substring(0,1).equals("//")) {
                url = "https:" + url;
            }
            imageUrls.add(url);
        }
    }

    public String getId() {
        return id;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getText() {
        return text;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImages(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public boolean hasVideo() {
        return hasVideo;
    }

    public String getParentPageUrl() {
        return parentPageUrl;
    }

    public int compareTo(Module m) {
        return this.id.compareTo(m.id);
    }

    public int compare(Module a, Module b) {
        return a.id.compareTo(b.getId());
    }

    public int compare(Module a, String s) {
        return 0;
    }

    public String getHtml() {
        return element.html();
    }
}