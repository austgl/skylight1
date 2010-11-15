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

import java.io.File;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Scraper that runs against file data instead of a live page.
 * 
 * @author lance
 */
public class FileDataScraper extends Scraper {
    /** Absolute URI to the local test file to use. */
    private static final String LOCAL_TEST_SIGN_IN_PAGE = 
        "file:///" + getDataPath() + "/example_market_console_dom.html";

    /**
     * Gets the Market page from the file.
     * @return market page
     */
    protected HtmlPage getMarketPage() {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        HtmlPage signedInPage = getRequiredPage(webClient, LOCAL_TEST_SIGN_IN_PAGE);
        return signedInPage;
    }
    
    /**
     * Gets the path to the data directory suitable for inclusion in a file URI.
     * @return path
     */
    public static String getDataPath() {
        // Hudson runs the unit tests from wsl_root
        File dataDir = new File("projects/MarketConsoleScraperTest/data");
        if (!dataDir.exists()) {
            // Eclipse runs the unit tests from the project subdirectory
            dataDir = new File("data");
        }
        return dataDir.getAbsolutePath().replace("\\", "/");
    }
}
