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

import java.util.Collection;

import junit.framework.TestCase;

import com.wsl.marketconsolescraper.model.AndroidMarketListing;

/**
 * Tests the market console scraper.
 * 
 * @author lance
 */
public class ScraperTest extends TestCase {
    /** 
     * Tests scraping a local file.
     */
    public void testScrapeLocalTestFile() {
        Scraper localTestScraper = new FileDataScraper();
        Collection<AndroidMarketListing> applicationsInfo = localTestScraper.scrape();
        flush();
        System.out.println("Retrieved application info: " + applicationsInfo);
        assertFalse(applicationsInfo.isEmpty());
    }

    /**
     * Tests running the scraper from the command line.
     */
    public void testCommandLineScrape() {
        Scraper scraper = new Scraper();
        Collection<AndroidMarketListing> applicationsInfo = scraper.scrape();
        flush();
        System.out.println("Retrieved application info: " + applicationsInfo);
        assertFalse(applicationsInfo.isEmpty());
    }

    /**
     * Flushes both standard out and standard error.
     * This helps keep error messages and normal output in sync in the console.
     */
    private static void flush() {
        System.out.flush();
        System.err.flush();
    }
}
