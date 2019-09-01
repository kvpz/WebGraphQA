package com.wgtest;

import com.wg.WebPage;
import org.junit.Assert;
import org.junit.Test;

public class WebPageTests {

    /**
     * Check if the proper id is created for a WebPage object created with the constructor using a URL argument. The
     * URL will be one with a suffixed host language query string.
     */
    @Test
    public void InitWithArgUrlWithHl() {
        WebPage webPage = new WebPage("https://store.google.com/be/product/nest_aware?hl=fr-be");

        Assert.assertTrue("storegooglecombeproductnest_awarehlfrbe".equals(webPage.GetId()));
        System.out.println(webPage.GetId());
        System.out.println("storegooglecombeproductnest_awarehlfrbe");
    }
}
