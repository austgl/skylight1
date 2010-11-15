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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wsl.marketconsolescraper.db.AndroidMarketListingSaver;
import com.wsl.marketconsolescraper.model.AndroidMarketListing;

/**
 * Runs scraper and inserts results into the DB.
 * 
 * @author lance
 */
public class ScrapePersistor {
    /** Used for logging. */
    private static final Log LOG = LogFactory.getLog(ScrapePersistor.class);
    
    /** Used to access the DB. */
    private AndroidMarketListingSaver dao;
    
    /**
     * Constructor.
     */
    public ScrapePersistor() {
        dao = new AndroidMarketListingSaver();
    }
    
    /**
     * Performs a scrape.
     * @return Collection<AndroidMarketListing> applications info
     */
    public Collection<AndroidMarketListing> scrapeIntoDb() {
        LOG.info("ScrapePersistor#scrapeIntoDb(): starting");
        Scraper scraper = new Scraper();
        Collection<AndroidMarketListing> applicationsInfo = scraper.scrape();
        dao.insertAll(applicationsInfo);
        if (LOG.isInfoEnabled()) {
            LOG.info("ScrapePersistor#scrapeIntoDb(): completed: " + applicationsInfo);
        }
        return applicationsInfo;
    }
}
