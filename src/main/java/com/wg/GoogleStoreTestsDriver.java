package com.wg;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;


public class GoogleStoreTestsDriver {

    /**
     Create a FirefoxDriver with options enabled.
     */
    static FirefoxDriver CreateFFDriver() {
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        firefoxBinary.addCommandLineOptions("--load-images=no");
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("permissions.default.image", 2);
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        firefoxOptions.setProfile(firefoxProfile);

        return new FirefoxDriver(firefoxOptions);
    }

    public static void main(String[] args) {
        WebPage webPage = new WebPage("https://store.google.com/config/artworks_my_case");
        FirefoxDriver fd = CreateFFDriver();
        fd.get(webPage.GetUrl());
        webPage.SetPageSource(fd.getPageSource());
        boolean result = GoogleStoreTests.Is404(webPage);
        System.out.println("404 result: " + result);


    }
}
