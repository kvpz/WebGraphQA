package gstore.module;

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
    private String dataName;
    private String dataTrackingModuleName;
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

        dataName = this.element.child(0).attr("data-name");

        if(this.element.childNodes().size() > 2) {
            dataTitle = this.element.child(2).attr("data-title");

            dataTrackingModuleName = this.element.child(2).attr("data-tracking-module-name");
        }
        if(this.element.getElementsByTag("video").size() > 0) {
            hasVideo = true;
        }

        initializeImageUrls();
    }

    /**
     * Get all the URLs for image assets and store them in List data member.
     * Image types: png, jpeg, webm
     *
     */
    private void initializeImageUrls() {
        imageUrls = new ArrayList<>();

        Elements imgElements = element.getElementsByTag("img");
        for(Element imgElement : imgElements) {
            String url = imgElement.attr("src");
            if(!url.isEmpty() && url.substring(0,1).equals("//")) {
                url = "https:" + url;
            }
            imageUrls.add(url);
        }

        Elements elsWithStyleAttr = element.getElementsByAttribute("style");
        for(Element elementWithStyle : elsWithStyleAttr) {
            String styleVal = elementWithStyle.attr("style");
            if(!styleVal.contains("url")) {
                continue;
            }

            String url = new String();
            if(styleVal.contains("http")) {
                // extract the url from the attribute
                int urlStartIndex = styleVal.indexOf("http");
                int urlEndIndex = styleVal.substring(urlStartIndex).indexOf(")");

                if(urlStartIndex > urlEndIndex)
                    urlEndIndex += urlStartIndex;

                url = styleVal.substring(urlStartIndex, urlEndIndex);
                if(url.endsWith("\""))
                    url = url.substring(0, url.length() - 1);
                imageUrls.add(url);
            }
            else {
                int indexOfBackslash = styleVal.indexOf("//");
                if(indexOfBackslash < 0) {
                    // There is no http or protocol relative url, thus there is an error
                    System.out.println("ERROR (" + indexOfBackslash + "): style background image: " + styleVal);
                    System.out.println("See " + parentPageUrl + "\n");
                    url = new String();
                }
                else {
                    url = styleVal.substring(indexOfBackslash, styleVal.indexOf(")"));
                    imageUrls.add("https:" + url);
                }
            }
        }

        Elements elsLazyImage = element.getElementsByAttribute("mqn-lazyimage-background-src");
        for(Element el : elsLazyImage) {
            String url = el.attr("mqn-lazyimage-background-src");
            if(!url.isEmpty() && url.substring(0,1).equals("//")) {
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

    public String getDataName() {
        return dataName;
    }

    public String getDataTrackingModuleName(){
        return dataTrackingModuleName;
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

    public boolean hasCarousel() {
        hasCarousel = false;
        if(element.html().contains("carousel")) {
            hasCarousel = true;
        }

        return hasCarousel;
    }
}
