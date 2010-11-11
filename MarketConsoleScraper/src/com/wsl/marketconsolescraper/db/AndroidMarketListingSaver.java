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

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.wsl.marketconsolescraper.model.AndroidMarketListing;

/**
 * Inserts scraped information about applications into the DB.
 * 
 * @author lance
 */
public class AndroidMarketListingSaver {
    /** Query to insert information for a single app. */
    private static final String INSERT_APP_INFO = 
        "INSERT INTO AndroidMarketStatistics (" + 
        "   application, number_of_ratings, active_installs, total_installs, " + 
        "   date, version, rating)" + 
        "VALUES (" + 
        "   :application, :numberOfRatings, :activeInstalls, :totalInstalls, " + 
        "   :date, :version, :rating)";
    
    private final DataSource dataSource;
    
    /**
     * Constructor.
     */
    public AndroidMarketListingSaver() {
        dataSource = ScraperDatabaseUtils.getDataSource();
    }
    
    /**
     * Inserts supplied information into the DB.
     * @param applicationsInfo Collection<AndroidMarketListing> with information for each app
     */
    public void insertAll(Collection<AndroidMarketListing> applicationsInfo) {
        for (AndroidMarketListing appInfo : applicationsInfo) {
            BeanPropertySqlParameterSource namedParameters 
                = new BeanPropertySqlParameterSource(appInfo);
            new NamedParameterJdbcTemplate(dataSource).update(INSERT_APP_INFO, namedParameters);
        }
    }
}
