package com.fqa;
import java.net.*;
import java.util.*;

/*
    Represents an HTML page.

 */
public class WebPage {

    UUID uuid;
    String url;
    String title; // id="main-title"
    String pageSource;
    java.util.Date lastUpdated;
    Boolean visited;

    WebPage() {

        visited = false;
    }

    WebPage(String url) {
        try {
            this.url = url;
            this.lastUpdated = new Date();
            this.uuid = MyUtil.urlToUUID(url);
            this.visited = false;
        }
        catch (Exception e) {
            System.out.println("From WebPage constructor");
            System.out.println(e);
        }

    }

    void SetTitle(String title) {
        this.title = title;
    }

    void SetPageSource(String source) {
        pageSource = source;
    }

    String GetPageSource() {
        return pageSource;
    }


}
