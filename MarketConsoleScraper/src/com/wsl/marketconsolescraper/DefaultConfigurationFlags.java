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
 * Defines configuration flags and their default values which can be overridden in the
 * LocalConfigurationFlags to suit each developers local testing environment.
 * See LocalConfigurationFlags for more details on how to use configuration flags.
 *
 * *******************************************************************************************
 * NOTE: Do not change this file unless you want to change the default values, which are used
 * in the final release versions.
 * *******************************************************************************************
 *
 * @author lance
 */
public class DefaultConfigurationFlags {
    /**
     * The path of the config file that contains properties that shouldn't be hard coded.
     * For example, the DB server to use should be changeable without recompiling the project.
     */
    public static String CONFIG_FILE_PATH =
        "/var/www/config/com/wsl/dashboard/scraper-config-server.properties";
}