package com.wg;

import java.util.HashMap;
import java.util.Map;

/**
    Contains the tests to perform on google store webpages
 */
public class GoogleStoreTests {

    /**
         Returns true if webPage is a 404 (which is not desirable!)
     */
    public static boolean Is404(WebPage webPage) {
        boolean status = false;
        status = webPage.GetPageSource().contains("404");
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
