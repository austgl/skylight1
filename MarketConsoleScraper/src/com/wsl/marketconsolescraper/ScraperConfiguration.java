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

package com.wsl.marketconsolescraper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.Base64Utils;
import com.mindprod.XorPWScrambler;

/**
 * Loads configuration from outside the application. Allows changing the values without 
 * making a new build each time. See WSL_ROOT/projects/MarketConsoleScraper/config for example 
 * files.
 * 
 * @author lance
 */
public class ScraperConfiguration {
    /** Used for logging. */
    private static final Log LOG = LogFactory.getLog(ScraperConfiguration.class);

    /** Required to avoid warning due to Properties implementing Serializable. */
    private static final long serialVersionUID = 1L;
    
    /** If the scraper has been configured. */
    private boolean isConfigured;
    
    /** Underlying properties. */
    private Properties properties = new Properties();

    /**
     * Constructor. Loads properties from location specified in LocalConfigurationFlags or
     * DefaultConfigurationFlags.
     */
    public ScraperConfiguration() {
        try {
            properties.load(new FileInputStream(LocalConfigurationFlags.CONFIG_FILE_PATH));
            isConfigured = true;
        } catch (FileNotFoundException e) {
            LOG.warn("Could not load properties from: " 
                    + LocalConfigurationFlags.CONFIG_FILE_PATH, e);
        } catch (IOException e) {
            LOG.warn("Could not load properties from: " 
                    + LocalConfigurationFlags.CONFIG_FILE_PATH, e);
        }
    }

    /** @return true if and only if the scraper config file was loaded */
    public boolean isConfigured() {
        return isConfigured;
    }

    /** @return delay before scraping the Market console for the first time, in milliseconds */
    public long getDelay() {
        return new Long(properties.getProperty("delay"));
    }

    /** @return delay between scraping the Market console, in milliseconds */
    public long getPeriod() {
        return new Long(properties.getProperty("period"));
    }

    /** @return URL of the DB to insert the scraper results */
    public String getDbUrl() {
        return properties.getProperty("dbUrl");
    }

    /** @return username to use with the DB */
    public String getDbUsername() {
        return properties.getProperty("dbUsername");
    }

    /** @return password to use with the DB */
    public String getDbPassword() {
        return properties.getProperty("dbPassword");
    }

    /** @return username to use with the market console */
    public String getMarketUsername() {
        return properties.getProperty("marketUsername");
    }

    /** @return password to use with the market console */
    public String getMarketPassword() {
        String plainPassword = getMarketPlainPassword();
        if (plainPassword != null) {
            return plainPassword;
        }
        String scrambledBase64Password = getMarketScrambledBase64Password();
        byte[] scrambledPasswordData = Base64Utils.fromBase64(scrambledBase64Password);
        char[] scrambledPasswordChars = new String(scrambledPasswordData).toCharArray();
        String password = XorPWScrambler.unscramble(scrambledPasswordChars);
        return password;
    }

    /** @return password to use with the market console */
    public String getMarketPlainPassword() {
        return properties.getProperty("marketPlainPassword");
    }

    /** @return password to use with the market console */
    public String getMarketScrambledBase64Password() {
        return properties.getProperty("marketScrambledBase64Password");
    }
}
