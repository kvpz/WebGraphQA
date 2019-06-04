package com.wgtest;

import com.wg.WebPage;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;

public class WGTest {
    public static List<String> getLinks(FirefoxDriver driver) {
        String[] tags = {"a", "area", "base", "link"};
        String[] attributes = {"href", "data-config-url", "ng-href"};
        List<String> links = new ArrayList<String>();
        for(int _i = 0; _i < 4; ++_i) {
            List<WebElement> linkElements = driver.findElementsByTagName(tags[_i]);
            for(WebElement webElement : linkElements) {
                try {
                    //System.out.println(webElement.getAttribute("outerHTML"));
                    String refLink = webElement.getAttribute("pathname"); // get the href value as seen in html
                    String aRefLink = webElement.getAttribute("href");
                    System.out.println(refLink);
                    System.out.println(aRefLink);
                    if (refLink != null && !refLink.equals("#")) {
                        if (!webElement.getAttribute("href").contains("storage.google.com")) // avoid appending the incorrect domain to a path
                            links.add("https://store.google.com" + refLink);
                    } else {
                        links.add(webElement.getAttribute("href")); // store hard link value assigned to href
                    }

                }
                catch(StaleElementReferenceException e){
                    System.out.println("Exception in getLinks" + e);
                }
            }
        }

        return links;
    }



    public static void main(String[] s) throws Exception {
        WebPage wpage = new WebPage("https://store.google.com/ca/product/test?hl=fr-CA");
        System.out.println(wpage.GetUrl());
        System.out.println(wpage.GetId());

        //WebGraphFile wgf = new WebGraphFile(wpage);
        //wgf.WriteWebPageToFile(wpage);

    }

    /*
        Click the buy button.

        classes the buy button can have:
        primary transaction button

     */
    boolean ClickBuyButton() {
        boolean truthy = false;

        return truthy;
    }
}
