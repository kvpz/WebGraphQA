package com.wg;

import java.util.HashMap;
import java.util.Map;
import java.net.*;
/**
    Contains the tests to perform on google store webpages
 */
public class GoogleStoreTests {

    /**
         Returns true if webPage is a 404 (which is not desirable!)

        Reference: https://www.guru99.com/find-broken-links-selenium-webdriver.html
     */
    public static boolean Is404(WebPage webPage) {
        boolean status = false;
        HttpURLConnection huc = null;

        try {
            huc = (HttpURLConnection) (new URL(webPage.GetUrl())).openConnection();

            huc.setRequestMethod("HEAD");

            huc.connect();

            int respCode = huc.getResponseCode();

            if(respCode >= 404)
                status = true;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return status;
    }

    /**
       Perform all the tests on a webPage
     */
    public static Map<String, Boolean> TestAll(WebPage webPage) {
        Map<String, Boolean> resultsMap = new HashMap<String, Boolean>();
        Boolean Is404Result = Is404(webPage);
        resultsMap.put("Is404", Is404Result);

        return resultsMap;
    }
}
