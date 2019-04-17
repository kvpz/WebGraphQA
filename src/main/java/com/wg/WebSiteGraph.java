package com.wg;

/*
    The class should be dedicated to a single domain.
    A page is considered unique. Rules may be bent for heavy SPA based sites.
    All links with a different subdomain are considered child nodes.
 */
public class WebSiteGraph {
    // member data
    String domain;
    Integer totalPages;
    //String graphDir = "/Users/kevin/Documents/googleStore/WebSiteGraph/";

    WebSiteGraph(String d) {
        domain = d;
    }


}
