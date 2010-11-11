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

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wsl.marketconsolescraper.ScraperConfiguration;

/**
 * Schedules the timer that starts the market console scraper regularly.
 * 
 * @author lance
 */
public class ScrapeScheduler implements ServletContextListener {
    /** Used for logging. */
    private static final Log LOG = LogFactory.getLog(Scraper.class);
    
    /** Manages the background thread for calling tasks. */
    private Timer timer = new Timer();
    
    /**
     * Cancels the timer. Called by the container when the app is undeployed.
     * @param context ServletContextEvent to comply with ServletContextListener interface
     */
    @Override
    public void contextDestroyed(ServletContextEvent context) {
        timer.cancel();
    }

    /**
     * Schedules the scrape to be run at a certain time. Called by the container when the 
     * app is deployed due to this class being specified as listener in web.xml . Scraping 
     * is scheduled and run from a web app like this in order to avoid using cron jobs, 
     * which are an additional thing that would have to be setup on any server we use it on.
     * 
     * @param context ServletContextEvent to comply with ServletContextListener interface
     */
    @Override
    public void contextInitialized(ServletContextEvent context) {
        LOG.info("ScrapeStarter: web app started, scheduling Market scraper.");
        ScraperConfiguration properties = new ScraperConfiguration();
        if (properties.isConfigured()) {
            timer.schedule(new ScrapeTimerTask(), 
                    properties.getDelay(), properties.getPeriod());
        }
    }
}
