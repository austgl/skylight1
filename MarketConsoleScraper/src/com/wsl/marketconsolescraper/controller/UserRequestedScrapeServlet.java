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

package com.wsl.marketconsolescraper.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wsl.marketconsolescraper.logic.ScrapePersistor;
import com.wsl.marketconsolescraper.model.AndroidMarketListing;

/**
 * Triggers ScrapePersistor when it receives a web request and outputs the results.
 * Used to request an immediate scrape. Normally a timer mechanism performs the scrapes 
 * and this class is not involved.
 * 
 * @author lance
 */
public class UserRequestedScrapeServlet extends HttpServlet {
    /**
     * Default value to comply with Serializable interface.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Processes a request.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("\nStarting scrape...");
        try {
            try {
                Collection<AndroidMarketListing> applicationInfo = 
                    new ScrapePersistor().scrapeIntoDb();
                out.println("\nScrape completed. Retrieved application info: " + applicationInfo);
            } catch (RuntimeException e) {
                out.println("\nScrape failed:");
                e.printStackTrace(out);
            }
            return;
        } catch(Exception t) {
            out.println("\nScrape failed:");
            t.printStackTrace(out);
            throw new RuntimeException(t);
        }
    }
}
