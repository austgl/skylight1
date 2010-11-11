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

package com.wsl.marketconsolescraper.logic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.wsl.marketconsolescraper.ScraperConfiguration;
import com.wsl.marketconsolescraper.model.AndroidMarketListing;
import com.wsl.marketconsolescraper.model.ElementStarsToString;
import com.wsl.marketconsolescraper.model.ListingDataItemRetriever;

/**
 * Scrapes Google Market's developer console for info about applications.
 *
 * @author lance
 */
public class Scraper {
    /** How long to wait for any scheduled JavaScript. */
    private static final int WAIT_FOR_DELAYED_JAVASCRIPT = 10 * 1000;

    /** Where the submit button is located in the sign-in page. */
    private static final String SIGN_IN_PAGE_SUBMIT_INPUT_XPATH = "//input[@type=\"submit\"]";

    /** Where the password input is located in the sign-in page. */
    private static final String SIGN_IN_PAGE_PASSWORD_INPUT_XPATH = "//input[@name=\"Passwd\"]";

    /** Where the email input is located in the sign-in page. */
    private static final String SIGN_IN_PAGE_EMAIL_INPUT_XPATH = "//input[@name=\"Email\"]";

    /** Page to sign-in to in order to get to the Market Console. */
    private static final String SIGN_IN_PAGE =
        "https://www.google.com/accounts/ServiceLogin?service=androiddeveloper&passive=true&nui=1" +
        "&continue=http%3A%2F%2Fmarket.android.com%2Fpublish" +
        "&followup=http%3A%2F%2Fmarket.android.com%2Fpublish";

    /** Text to check for when sign-in fails. */
    private static final String SIGN_IN_PAGE_FAILED_TEXT =
        "The username or password you entered is incorrect.";

    /** Error message for when a required page could not be loaded. */
    private static final String COULD_NOT_LOAD_REQUIRED_PAGE_MESSAGE = "Could not load page: ";

    /** What information is extracted from each application, where it is, and how to process it. */
    private static final List<ListingDataItemRetriever> mappings = Arrays.asList(
        new ListingDataItemRetriever[] {
            new ListingDataItemRetriever(
                    "//div[@class='listingRow']//a[contains(@href, '#EDIT_APPLICATION?')]") {
                        @Override
                        public void setProperty(HtmlElement rawItem,
                                AndroidMarketListing listing) {
                            if (rawItem == null) {
                                return;
                            }
                            listing.setApplication(rawItem.asText().trim());
                        }
            },
            new ListingDataItemRetriever(
                    "//div[@class='listingRow']//span[@example='1.0']") {
                @Override
                public void setProperty(HtmlElement rawItem,
                        AndroidMarketListing listing) {
                            if (rawItem == null) {
                                return;
                            }
                            listing.setVersion(rawItem.asText().trim());
                        }
            },
            new ListingDataItemRetriever(
                    "//table[@class='listingRating']//span[@class='gwt-InlineLabel']") {
                @Override
                public void setProperty(HtmlElement rawItem,
                        AndroidMarketListing listing) {
                            if (rawItem == null) {
                                return;
                            }
                            String text = rawItem.asText()
                                .replace('(', ' ').replace(')', ' ').trim();
                            listing.setNumberOfRatings(new Integer(text));
                        }
            },
            new ListingDataItemRetriever(
                    "//div[@class='listingRow']//span[@example='12,000']") {
                @Override
                public void setProperty(HtmlElement rawItem,
                        AndroidMarketListing listing) {
                            if (rawItem == null) {
                                return;
                            }
                            String text = rawItem.asText().trim();
                            listing.setTotalInstalls(new Integer(text));
                        }
            },
            new ListingDataItemRetriever(
                    "//div[@class='listingRow']//span[@example='9,000']") {
                @Override
                public void setProperty(HtmlElement rawItem,
                        AndroidMarketListing listing) {
                            if (rawItem == null) {
                                return;
                            }
                            String text = rawItem.asText().trim();
                            listing.setActiveInstalls(new Integer(text));
                        }
            },
            new ElementStarsToString("//table[@class='listingRating']")
        }
    );

    /** Used for logging. */
    private static final Log LOG = LogFactory.getLog(Scraper.class);

    /**
     * Performs a scrape.
     * @return scraped application data
     */
    public Collection<AndroidMarketListing> scrape() {
        log("Scraper starting.");

        final HtmlPage signedInPage = getMarketPage();

        log("Market page title: " + signedInPage.getTitleText());

        String pageAsText = signedInPage.asText();
        log("Market page text: " + pageAsText);

        LinkedList<AndroidMarketListing> applicationInfo = new LinkedList<AndroidMarketListing>();

        for (ListingDataItemRetriever item : mappings) {
            ListIterator<AndroidMarketListing> appInfoIterator = applicationInfo.listIterator();
            item.retrieveAppInfoItems(signedInPage, appInfoIterator);
        }
        log("Application info map: " + applicationInfo);

        return applicationInfo;
    }

    /** Logs a message.
     * @param message String to log
     */
    private static void log(String message) {
        LOG.debug(message);
    }

    /**
     * Gets a page that is required to continue scraping.
     * @param webClient WebClient to get it with
     * @param url String location of the page
     * @return specified page
     * @throws RuntimeException if the page couldn't be retrieved
     */
    protected static HtmlPage getRequiredPage(WebClient webClient, String url) {
        HtmlPage signedInPage;
        try {
            signedInPage = webClient.getPage(url);
        } catch (FailingHttpStatusCodeException e) {
            throw new RuntimeException(COULD_NOT_LOAD_REQUIRED_PAGE_MESSAGE + url, e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(COULD_NOT_LOAD_REQUIRED_PAGE_MESSAGE + url, e);
        } catch (IOException e) {
            throw new RuntimeException(COULD_NOT_LOAD_REQUIRED_PAGE_MESSAGE + url, e);
        }
        return signedInPage;
    }

    /**
     * Gets the current Market page from the web.
     * @return market page
     */
    protected HtmlPage getMarketPage() {
        // Get sign-in page.
        final WebClient webClient = new WebClient();
        try {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.setThrowExceptionOnScriptError(true);

            HtmlPage signInPage = getRequiredPage(webClient, SIGN_IN_PAGE);
            webClient.waitForBackgroundJavaScriptStartingBefore(WAIT_FOR_DELAYED_JAVASCRIPT);
            log("Retrieved sign-in page, title: " + signInPage.getTitleText());

            ScraperConfiguration config = new ScraperConfiguration();
            
            // Fill in login form.
            HtmlTextInput emailInput = (HtmlTextInput)
                signInPage.getFirstByXPath(SIGN_IN_PAGE_EMAIL_INPUT_XPATH);

            String marketUsername = config.getMarketUsername();
            System.out.println("Scraper using username: " + marketUsername);
            emailInput.setValueAttribute(marketUsername);
            HtmlPasswordInput passwordInput = (HtmlPasswordInput)
                signInPage.getFirstByXPath(SIGN_IN_PAGE_PASSWORD_INPUT_XPATH);
            
            String marketPassword = config.getMarketPassword();
            passwordInput.setValueAttribute(marketPassword);

            // Submit login form.
            HtmlSubmitInput signInInput = (HtmlSubmitInput)
                signInPage.getFirstByXPath(SIGN_IN_PAGE_SUBMIT_INPUT_XPATH);
            HtmlPage signedInPage;
            try {
                signedInPage = signInInput.click();
            } catch (IOException e) {
                throw new RuntimeException("Error submitting sign-in page.", e);
            }
            webClient.waitForBackgroundJavaScriptStartingBefore(WAIT_FOR_DELAYED_JAVASCRIPT);

            log("Page title after sign-in attempt: " + signedInPage.getTitleText());
            String pageAsText = signedInPage.asText();
            System.out.println("Signed in page text: " + pageAsText);
            
            boolean signInFailed = pageAsText.contains(SIGN_IN_PAGE_FAILED_TEXT);
            if (signInFailed) {
                throw new RuntimeException("sign-in failed. " + SIGN_IN_PAGE_FAILED_TEXT);
            }
            return signedInPage;
        } finally {
            webClient.closeAllWindows();
        }
    }
}
