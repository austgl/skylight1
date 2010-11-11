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

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Converts star images to a floating point rating.
 * 
 * @author lance
 */
public class ElementStarsToString extends ListingDataItemRetriever {
    /** String that indicates a full star in Firefox DOM. */
    private static final String FULL_STAR_FIREFOX = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAAQCAYAAADwMZRfAAABQUlEQVR42mNgoDUo6DcQKOvXvA+iyTakrF+roWa69n8QTbYrqqZovV9y2uI/iCbLNSDbJ24x+r/2ltX/SUCaaNeU9msZVPRrOIA0gGxffd3y/7o7Vv9BNIgPEgfJg9RhsVXzPMjvnSv0/0/cZPR/zmHT/0vOmYMNgGEQf84R0/8TNxuB1UHCSvM8igsqJ2u9X3DU7P+a61YomtHx2htW/xccM/8PUo/hIpAA0MnnF50yx2vI4tPmIK+dx+oleIxM1ToPcjY2A+YCxUHyBGMKFHjzj5thNWTBCTPiYgkYWOuXnLH4vwbo97W3gZqheO1Nq/9Lz1qAAnM9MYacX3nB8v+SUxb/u1bpgzTdB9GgRAcSR4kRXKB+ls7/jmUQzUAcADU4AMTvBIqD5PEaAElomvdL+zUSsCdGjQSQPEgd1XM7ALFpFBAsKas7AAAAAElFTkSuQmCC";
    
    /** String that indicates a full star in HTMLUnit. */
    private static final String FULL_STAR_HTML_UNIT = "-188px -75px";
    
    /** String that indicates a half star in Firefox DOM. */
    private static final String HALF_STAR_FIREFOX = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAAQCAYAAADwMZRfAAABRklEQVR42mNgoDUo6DcQKOvXvA+iyTakrF+rYcbq+P8gmmxXVE3Ref/wzbH/9dPM35PlGpDtU7db/H/349r/3cenEO+a0n4tg4p+DQeQhqopWu/X3XAAG/Ltxyewa0DiIHmQOiy2ap6vmab9v2Op/v/+dYb/Z+01/b/olPn/jXedwIaAwNNX1/9fubMH7KqVOyv/t891+Q/Sh+KCysla7+fuM/u/4qzl/1WXrP6vuW6FYggyABkIchmGi0ACQC+cn3fIDK8hIAMaZ1iex+olRIxonZ+1zxSrISDvgAwgGFOgwJsLdA02Q+4+PkVcLAEDa/2SUxZYDXn38QkoMNcTY8h5UJgsPWfxv2eNIUjTfVBsgAwAAZQYwQXqZ+v871yhD9YMxAFQgwNghk1YEvgfrwGQhKZ5v7RfIwF7YtRIAMmD1FE9twMAOsY8BvBKjakAAAAASUVORK5CYII=";
    
    /** String that indicates a half star in HTMLUnit. */
    private static final String HALF_STAR_HTML_UNIT = "-171px -75px";
    
    /** 
     * {@inheritDoc}
     */
    public ElementStarsToString(String xPath) {
        super(xPath);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void setProperty(HtmlElement rawItem, AndroidMarketListing listing) {
        if (rawItem == null) {
            return;
        }
        List<HtmlElement> images = rawItem.getHtmlElementsByTagName("img");
        float rating = 0;
        for (HtmlElement image : images) {
            String style = image.getAttribute("style");
            if (style.contains(FULL_STAR_FIREFOX) || style.contains(FULL_STAR_HTML_UNIT)) {
                rating += 1;
            } else if (style.contains(HALF_STAR_FIREFOX) 
                    || style.contains(HALF_STAR_HTML_UNIT)) {
                rating += 0.5;
                break;
            }
        }
        listing.setRating(rating);
    }
}