# About the packages

What does WG stand for?  Web Graph.  Why? Because it is the best quip I could use to describe what I am working towards.

I. About the Code
------------------

A. Package com.wg

This folder will contain all the functions and programs for testing large portions of the website.  Tests here are
considered generic.  If the test cannot be applied across different websites, then it should not go here.  There should
not be any code that is written directly against a specific website.  As I write this sentence, the majority of the code
 is dedicated to the google store.  The goal is to make these tests compatible with different websites. Refactors
 pull requests are welcome.


B. Package com.wgtest

This folder will contain unit tests against the functions written under com.wg.


C. Package gstore
This folder is dedicated to gstore and deserves its own repo.  It is a revamp of what is currently found under com.wg.
The difference is that com.wg is a set of tests that can be run across various sets of data such as distinct groups of
URLs or directories.  The code under gstore will focus on writing tests (specifically) against what is available, that
is, Pages, Modules, Carousels, etc. Notice how Pages encapsulate modules, modules can encapsulate caorusels, and so on.
 Right now I am writing tests that can be applied to various pages.  If there is a test that is specific and unique to
 one page, then a separate class should be written containing the test(s) for that page.  Tests should be written as
 specific to a page element as possible. Generally it would be a good idea to write tests for modules and not entire
 pages.

1. class DiffMachine
This class was originally created with the purpose of finding differences across each of the pages for a region from
different dates.  For example, I want to check if there are any differences on the Nest Hub page from last week and
today.
I'm thinking about making this a program.  Differences that can be found across 2 pages or modules should be applied
to a respected diff class, PageDiff or ModuleDiff.

2. class PageDiff
Contains methods that can find differences between two pages.  Please avoid writing tests that step into Module territory.
Contains a method to check if two pages have the same amount of modules.
Contains a method to generate reports about the diff results.
Diffs can be found between two completely different pages, or pages that are under different URLs.


II. Software Design
--------------------
This documentation was started after about 2 months writing tests against store.google.com.  Over this time period, I
have expanded and altered the design.  Some reasons for the code structure and use can be found scattered across the
code in comment blocks.

Work in progress.

III. QA Test Scopes
--------------------

A. Accessibility
Check for aria label values.  There is a test that will get all the values for aria label across the entire website and
print them out.  I would then read them checking for inappropriate characters, or text that does not match


B. Localization

1. Currency symbols.  There are several methods to test for this.  If a localized site only accepts the '$' symbol, then
 the localized pages can be checked for symbols that are not supposed to be there.  If the program finds other currency
 symbols, then there is a bug on that page.  This test is the simplest to implement.  Another test can be performed by
 extracting the copy that pertains to product pricing.  This test could possibly miss on other pricing occurrences on
 the page because a new module may have been added to the page and the test is not designed to check this module.

2. Product pricing. Pages that contain pricing information must be analyzed.  Tests for this must be updated if the
elements on the page that contain pricing information is changed.  While writing these tests, first list the pages that
have pricing information.  Finding pages with pricing information can be done by checking for currency symbols.  Then
scan and record the elements that contain the pricing information.  These elements can then be queried when performing a
 regression on the page and checked for the correct pricing information.
 [GStore specific]:  the product detail pages should all contain pricing information next to the transaction button.
 The functional string that is supposed to appear in that area should be recorded in a text file dedicated to a single
 product in a region and compared against what is on the actual website. This test is important because often times
 the string is not correctly listed for all the PDPs after an offer for the product is placed.

3. Content.  The content needs to be intended for the localized page.  A manual test will still need to be performed to
check for this, but there are some methods for detecting if content should not be on a page.  The copy can be checked
for strings that should not be present, such as names of products that are not available in a country.

4. Grammar.  French requires a space between certain punctuations.


Differential testing is very important.  Various comparisons can be applied against a page across multiple regions. For
example, if a page is internationalized, or the URL path minus the country code is similar, a comparison between each of
 the pages can be done to:
  - identify similarities that are not supposed to be there, such as English copy in French version of page
  -

    It's very important to test if there is a difference between the module amount and IDs of the current version of a webpage and the last version downloaded.
    1) does page from a region differ from an older version of the page (True/ False)
    2) does a page's module count differ from an older version of the page (True/ False)
    3) does a specific module's (look up by id) copy differ from an older version
    4)

C. UI

1. Check for disappearing nav bar. There is a nav bar on the PDPs that would disappear after scrolling down the page
       and resizing the viewport.  It would reappear after scrolling back to the top and resizing the browser.  As of 07/05/2019, this is still an issue.
2. link analysis.  Pages should always have a link.  All links should link to a valid page (not a 404 or redirect, which
 may signal as a 500 error)
3. TODO: automatic slideshow (or carousel) rotation speed.  Each element should be displayed for the same amount of time
.  I noticed that the rotations may vary for some carousels after
        scrolling up and down a page rapidly a few times.  A page that was victim of such a carousel is the Overview PDP for the Google Nest Learning Thermostat.



D. Content
This may overlap with testing for localization.

1. Images need to belong on the page.

2. A page's meta description element (found in the head element) <meta description="...">.  This copy is critical
because it can be seen on a search engine's results page.
        * the value for the content attributes of the meta elements found in the HTML head.  Besides localization, it's important to make sure that these are relevant to the page.
        * values of the alt attributes. Again, it is important that these are relevant to the element they are a part of.

## Test functions:
    1) TODO: Auto-rotating carousel detection: be able to identify whether an element contains a carousel/ slideshow.  A method could be written that will return a signal if an element has undergone changes, or values
        have been updated.  I could start by the body HTML element, and every time a change is detected, I would proceed to the next child element.  This process can be continued
        until a change occurs in the currently selected outer html (the value for one of the tag's attributes would have to be changed).  Once one of these elements are detected (after a
        period of about 10 seconds), log the current element in a file  (for manual verification) to guarantee that it is not analyzed again.  Then, crawl back up the current
        branch (until a sibling can be found).  The repeat the process of crawling down the sibling branch until another autorotating carousel is detected or there is nothing left to crawl.
        This can all be packed into a function that will return an array of the outer html of elements.
    2) Get all modules.  Modules are div elements with a class attribute with the value "page-module."  This can be found under the Modules class.

### Differential Testing
To determine if a page should be tested, I am checking portions of the page for updates.  The diff machine checks if any of the modules have been updated.  The page modules
contain all of the UI content including the global nav bar and the footer.  These module elements can be identified by the style class 'page-module'.  Nonces are applied to some of these
modules.  page modules that contain nonces which would create a false positive in a diff test have no value assigned to the id attribute.  That is why the diff machine is not performing
a diff on page modules that do not have an assigned id. Another id that changes per every request is the id value assigned to the "Need product advice" module which can be found
on the homepage.  Every other modules exerts no difference other than this module, just because of the id value.

Similar projects for inspiration
http://webgraph.di.unimi.it/

README last updated 2019-07-26.
Info about package gstore was added.
Currently accepting refactors for package com.wg.  Please read description above for what is expected.
If writing test cases for websites, please also include a unit test for the test under com.wgtest.
Any questions, #ping me!

