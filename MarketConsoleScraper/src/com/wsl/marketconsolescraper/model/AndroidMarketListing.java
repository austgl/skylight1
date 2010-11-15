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

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An app listed in the Market Publisher Console.
 * 
 * @author lance
 */
public class AndroidMarketListing {
    /** Application name. */
    private String application;
    
    /** Number of ratings. */
    private int numberOfRatings;
    
    /** Active installs. */
    private int activeInstalls;
    
    /** Total installs. */
    private int totalInstalls;
    
    /** Date information was retrieved. */
    private Date date = new Date();
    
    /** Version of application. */
    private String version;
    
    /** Rating. */
    private float rating;
    
    /**
     * Constructor.
     */
    public AndroidMarketListing() {
    }
    
    /** @return the application name */
    public String getApplication() {
        return application;
    }
    
    /** @return the number of ratings */
    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    /** @return the number of active installs */
    public int getActiveInstalls() {
        return activeInstalls;
    }

    /** @return the number of total installs */
    public int getTotalInstalls() {
        return totalInstalls;
    }

    /** @return the date the information was retrieved */
    public Date getDate() {
        return date;
    }

    /** @return the version of the application */
    public String getVersion() {
        return version;
    }

    /** @return the raring */
    public float getRating() {
        return rating;
    }

    /**
     * Sets the application name.
     * @param application String application name
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Sets the number of ratings.
     * @param numberOfRatings int number of ratings
     */
    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    /**
     * Sets the number of active installs.
     * @param activeInstalls int number of active installs
     */
    public void setActiveInstalls(int activeInstalls) {
        this.activeInstalls = activeInstalls;
    }

    /**
     * Sets the number of total installs.
     * @param totalInstalls int number of total installs
     */
    public void setTotalInstalls(int totalInstalls) {
        this.totalInstalls = totalInstalls;
    }

    /**
     * Sets the date the information was retrieved.
     * @param date Date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets the version of the application.
     * @param version String version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Sets the application's rating.
     * @param rating float rating
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /** @return string detailing the contents */
    public String toString() {
        return new ToStringBuilder(this).
            append("application", application).
            append("numberOfRatings", numberOfRatings).
            append("activeInstalls", activeInstalls).
            append("totalInstalls", totalInstalls).
            append("version", version).
            append("rating", rating).
            append("date", date).
            toString();
    }
}
