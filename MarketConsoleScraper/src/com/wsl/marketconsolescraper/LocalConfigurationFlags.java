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


/**
 * This file is used to override configuration flags to suit each developers local
 * testing environment.
 * If you want to add a new flag that needs to be overridden in your local configuration
 * you need to add this in the DefaultConfigurationFlags and in this file.
 *
 * Here is a small sample for adding the new flag 'boolean myNewFlag' which should
 * be false by default, but you want it to be true in your local configuration:
 *
 * First, add the flag to the DefaultConfigurationFlags:
 *
 *           static boolean myNewFlag = false;
 *
 * Second, add it to your LocalConfigurationFlags (which is this file):
 *
 *           static boolean myNewFlag = true;
 *
 * Third, use it wherever you need, e.g. like this
 *
 *           if (LocalConfigurationFlags.myNewFlag) {
 *             ...
 *           }
 *
 * ***************************************************
 * NOTE: Changes to this file must never be committed.
 * ***************************************************
 *
 * @author lance
 */
public class LocalConfigurationFlags extends DefaultConfigurationFlags {
    /**
     * To test the market scraper on your local machine create a config file that specifies
     * alternative property values from the one specified in DefaultConfigurationFlags .
     */
    public static String CONFIG_FILE_PATH = 
        "C:\\my_workspace\\MarketConsoleScraper\\config\\scraper-config.properties";
}