package skylight1.marketapp.feed;

import android.util.Log;
import skylight1.marketapp.model.EquityPricingInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class YahooEquityPricingInformationFeed extends AbstractEquityPricingInformation {
    private static String TAG = "YAHOO";


    public YahooEquityPricingInformationFeed() {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Set<EquityPricingInformation> setOfEquityPricingInformation = new HashSet<EquityPricingInformation>();

                Log.i(YahooEquityPricingInformationFeed.class.getName(), "Yahoo we are");

                try {
                    // Get all observed tickers
                    Set<String> allTickers = getTickers();

                    // return if no observed tickers
                    Log.i(TAG, "Observed tickers:" + allTickers);
                    if (allTickers.isEmpty()) {
                        Log.i(TAG, "No observed tickers");
                        return;
                    }

                    // Contact Yahoo for pricing
                    StringBuffer tickerList = new StringBuffer();
                    for (String ticker : allTickers) {
                        if (tickerList.length() != 0) {
                            tickerList.append(",");
                        }
                        tickerList.append(ticker);
                    }

                    String url = "http://download.finance.yahoo.com/d/quotes.csv?s="
                            + tickerList.toString()
                            + "&f=sb2b3jkm6";
                    Log.i(TAG, url);
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    //
                    // Loop over Yahoo response and extract pricing information

                    while ((line = reader.readLine()) != null) {
                        // AAPL",245.29,245.10,124.55,272.46,+12.76
                        String[] parts = line.split(",");
                        String ticker = parts[0].replace("\"",""); // replaceAll didn't work!! Will do regex later

                        Log.i(TAG, "TICKER:" + ticker);
                        Log.i(TAG, line);
                        EquityPricingInformation information = new EquityPricingInformation(ticker, ticker,
                                new BigDecimal(parts[1]), null, new Date(), new BigDecimal(parts[2]),
                                null, new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

                        Log.i(TAG, "Object not NULL?" + information);
                        setOfEquityPricingInformation.add(information);
                    }
                    Log.i(TAG, "Notfiying everyone");
                    notifyObservers(setOfEquityPricingInformation);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to get stock quotes", e);
                }
            }
        }, 5000, 60000);
    }
}