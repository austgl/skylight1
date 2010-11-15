/*
 * Copyright (C) 2010 WorkSmart Labs, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wsl.marketconsolescraper.model;

import java.util.List;
import java.util.ListIterator;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Retrieves an item of data for each application. For example, one subclass might retrieve the 
 * number of downloads for each application listed on a page.
 * 
 * @author lance
 */
public abstract class ListingDataItemRetriever {
    /** The location of the data item in the market console web page. */
    private String xPath;
    
    /**
     * Constructor.
     * @param xPath String location of the data item
     */
    public ListingDataItemRetriever(String xPath) {
        this.xPath = xPath;
    }
    
    /**
     * Retrieves the items from a page.
     * @param signedInPage HtmlPage to get items from
     * @param listingIterator ListIterator<AndroidMarketListing> with listings
     */
    @SuppressWarnings("unchecked")
    public void retrieveAppInfoItems(HtmlPage signedInPage, 
            ListIterator<AndroidMarketListing> listingIterator) {
        List<HtmlElement> rawElements = (List<HtmlElement>) signedInPage.getByXPath(xPath);
        for (HtmlElement rawElement : rawElements) {
            AndroidMarketListing listing = getOrCreateNextListing(listingIterator);
            setProperty(rawElement, listing);
        }
    }
    
    /**
     * Sets the listing property for this retriever using the supplied HtmlElement.
     * @param rawItem HtmlElement extracted from the page
     * @param listing AndroidMarketListing to set the property on
     */
    protected abstract void setProperty(HtmlElement rawItem, AndroidMarketListing listing);
    
    /**
     * Gets the next AndroidMarketListing instance or creates one if there isn't one.
     * @param listingIterator ListIterator<AndroidMarketListing> to get from and add to
     * @return non-null AndroidMarketListing instance
     */
    private AndroidMarketListing getOrCreateNextListing(
            ListIterator<AndroidMarketListing> listingIterator) {
        if (listingIterator.hasNext()) {
            return listingIterator.next();
        }
        AndroidMarketListing created = new AndroidMarketListing();
        listingIterator.add(created);
        return created;
    }
}