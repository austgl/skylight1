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

package com.wsl.marketconsolescraper.db;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.wsl.marketconsolescraper.ScraperConfiguration;

/**
 * Basic scraper database utility functions.
 * 
 * @author lance
 */
public class ScraperDatabaseUtils {
    /** DB driver class to use. */
    private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

    /**
     * Reads the server address from the configuration file
     * located at LocalConfigurationFlags.CONFIG_FILE_PATH.
     * See DefaultConfigurationFlags.CONFIG_FILE_PATH for more information.
     */
    @SuppressWarnings("deprecation")
    public static DataSource getDataSource() {
        ScraperConfiguration properties = new ScraperConfiguration();
        return new DriverManagerDataSource(
                MYSQL_DRIVER_CLASS,
                properties.getDbUrl(),
                properties.getDbUsername(), 
                properties.getDbPassword());
    }
}