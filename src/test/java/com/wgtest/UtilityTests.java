package com.wgtest;

import com.wg.MyUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests written for utility functions, i.e. those that are used very often for common operations.
 */
public class UtilityTests {

    @Test
    public void CreateURLFromWebpageDirName_TestValidInput_DomainOnly() {
        String valid_result = "https://store.google.com/";
        String result = MyUtil.CreateURLFromWebpageDirName("storegooglecom");
        Assert.assertTrue(result + " does not equal " + valid_result, result.equals(valid_result));
    }

    @Test
    public void CreateURLFromWebPageDirName_TestValidInput_DomainWithLocalPath() {
        String valid_result = "https://store.google.com/us";
        String result = MyUtil.CreateURLFromWebpageDirName("storegooglecomus");
        Assert.assertTrue(result + " does not equal " + valid_result, result.equals(valid_result));
    }

    /**
     * The host language query string should be properly appended to the resulting URL.
     * Regions with a second host language (all of which should be tested) are:
     * FR_CA, FR_BE, FR_CH, ZH_HK
     */
    @Test
    public void CreateURLFromWebpageDirName_TestValidInput_DomainWithHostLangQuery() {
        // name that does not start with the website's domain name
        String hk_test_input = "storegooglecomhkproductchromecasthlzhhk";
        String ca_test_input = "storegooglecomcaproductchromecasthlfrca";
        String be_test_input = "storegooglecombeproductchromecasthlfrbe";
        String ch_test_input = "storegooglecomchproductchromecasthlfrch";

        String hk_expected_result = "https://store.google.com/hk/product/chromecast?hl=zh-hk";
        String ca_expected_result = "https://store.google.com/ca/product/chromecast?hl=fr-ca";
        String be_expected_result = "https://store.google.com/be/product/chromecast?hl=fr-be";
        String ch_expected_result = "https://store.google.com/ch/product/chromecast?hl=fr-ch";

        String result = MyUtil.CreateURLFromWebpageDirName(hk_test_input);
        Assert.assertTrue(result + " does not match " + hk_expected_result, result.equals(hk_expected_result));

        result = MyUtil.CreateURLFromWebpageDirName(ca_test_input);
        Assert.assertTrue(result + " does not equal " + ca_expected_result, result.equals(ca_expected_result));

        result = MyUtil.CreateURLFromWebpageDirName(be_test_input);
        Assert.assertTrue(result + " does not equal " + be_expected_result, result.equals(be_expected_result));

        result = MyUtil.CreateURLFromWebpageDirName(ch_test_input);
        Assert.assertTrue(result + " does not equal " + ch_expected_result, result.equals(ch_expected_result));
    }

    /**
     * If a hostname other than the current website's (store.google.com) is used, then no URL should be returned.
     */
    @Test
    public void CreateURLFromWebpageDirName_TestInvalidInput_Hostname() {
        String test_input = "wwwgooglecom";
        String result = MyUtil.CreateURLFromWebpageDirName(test_input);
        Assert.assertFalse(result + " is not null for input" + test_input, result != null);
    }

    /**
     * If a path that is not supposed to be available with the current hostname, then a null string should be returned.
     */
    @Test
    public void CreateURLFromWebpageDirName_TestInvalidInput_InvalidPath() {
        // test an invalid region at start of path
        String test_input = "storegooglecompo";
        String result = MyUtil.CreateURLFromWebpageDirName(test_input);
        Assert.assertFalse(result + " is not null for input " + test_input, result != null);

        // test a valid region in path followed by an invalid subpath
        test_input = "storegooglecomusnot";
        result = MyUtil.CreateURLFromWebpageDirName(test_input);
        Assert.assertFalse(result + " is not null for input " + test_input, result != null);

        // test a valid path ("product") with an invalid subpath
        //test_input = "storegooglecomusproductnot";
        //result = MyUtil.CreateURLFromWebpageDirName(test_input);
        //Assert.assertFalse(result + " is not null for input " + test_input, result != null);

        // test a valid product path ("product/pixel_3") followed by invalid path
        //test_input = "storegooglecomusproductpixel_3apple";
        //result = MyUtil.CreateURLFromWebpageDirName(test_input);
        //Assert.assertFalse(result + " is not null for input " + test_input, result != null);
    }

    @Test
    public void CreateURLFromWebpageDirName_TestValidInput() {
        String collectionOffers = "storegooglecomcollectionoffers";
        String collectionOffersUrl = "https://store.google.com/collection/offers";
        String collectionOffersCreatedUrl = MyUtil.CreateURLFromWebpageDirName(collectionOffers);

        Assert.assertTrue(collectionOffersCreatedUrl + " does not equal " + collectionOffersUrl,
                collectionOffersUrl.equals(collectionOffersCreatedUrl));

        String magazineOffers = "storegooglecommagazineoffers";
        String magazineOffersUrl = "https://store.google.com/magazine/offers";
        String magazineOffersCreatedUrl = MyUtil.CreateURLFromWebpageDirName(magazineOffers);

        Assert.assertTrue(magazineOffersCreatedUrl + " does not equal + " + magazineOffersUrl,
                magazineOffersUrl.equals(magazineOffersCreatedUrl));

        String configTest = "storegooglecomconfigtest";
        String configTestUrl = "https://store.google.com/config/test";
        String configTestCreatedUrl = MyUtil.CreateURLFromWebpageDirName(configTest);

        Assert.assertTrue(configTestCreatedUrl + " does not equal " + configTestUrl,
                configTestUrl.equals(configTestCreatedUrl));

        String homeUs = "storegooglecomus";
        String homeUsURL = "https://store.google.com/us/";
        String homeUsCreatedUrl = MyUtil.CreateURLFromWebpageDirName(homeUs);

        Assert.assertTrue(homeUsCreatedUrl + " does not equal " + homeUsURL,
                homeUsURL.equals(homeUsCreatedUrl));
    }
}
