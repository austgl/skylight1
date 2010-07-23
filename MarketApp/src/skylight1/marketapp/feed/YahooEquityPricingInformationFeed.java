package skylight1.marketapp.feed;

import android.util.Log;
import skylight1.marketapp.EquityTimeSeries;
import skylight1.marketapp.model.CompanyDetail;
import skylight1.marketapp.model.EquityPricingInformation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class YahooEquityPricingInformationFeed extends AbstractEquityPricingInformation {
    private static final String TAG = YahooEquityPricingInformationFeed.class.getSimpleName();


    /*
     * Retrieve the basic information that is displayed on a watch list page.
     *  symbol, name, last price, market cap, price change, percent change
     *
     * @param aTicker The ticker symbol to request
     *
     */
    public void getBasicTickerInfo(String aTicker) {
        //TODO: How to return the information?  Create an new class?


        /*
        * s = symbol
        * n = name
        * l1 = last price
        * j1 = market capitalization
        * c - change and percent change
        */
        String url = "http://download.finance.yahoo.com/d/quotes.csv?s="
                + aTicker
                + "&f=snl1j1c";
//    Log.i(TAG,url);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            //
            // Loop over Yahoo response and extract pricing information

            while ((line = reader.readLine()) != null)

            {
                // AAPL",245.29,245.10,124.55,272.46,+12.76
                String[] parts = line.split(",");
                String ticker = parts[0].replace("\"", ""); // replaceAll didn't work!! Will do regex later
                String[] change = parts[4].replace("\"", "").split("-");
                String priceChangeStr = change[0].trim();
                String percentChangeStr = change[1].trim();
                Log.i(TAG, "(ticker,last)=>(" + ticker + "," + parts[1] + "," + parts[2]
                        + "," + parts[3]
                        + "," + priceChangeStr
                        + "," + percentChangeStr
                        + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTickerPrices() {
        
    }
    /*
     *
     */
    public YahooEquityPricingInformationFeed() {

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Set<EquityPricingInformation> setOfEquityPricingInformation = new HashSet<EquityPricingInformation>();

//                Log.i(YahooEquityPricingInformationFeed.class.getName(), "Yahoo we are");

                try {
                    // Get all observed tickers
                    Set<String> allTickers = getTickers();

                    // return if no observed tickers
                    if (allTickers.isEmpty()) {
//                        Log.i(TAG, "No observed tickers");
                        return;
                    }
                    // Only show message if we have observed tickers
                    Log.i(TAG, "Observed tickers:" + allTickers);

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
                            + "&f=sb2b3jkm6c1p2";
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

                        String ticker = parts[0].replace("\"", ""); // replaceAll didn't work!! Will do regex later

                        Log.i(TAG, "TICKER:" + ticker);
                        Log.i(TAG, line);
                        if(parts[1].equals("N/A")) {
                        	Log.i(TAG,"DIDN'T FIND TICKER");
                        } else {
                        	EquityPricingInformation information = new EquityPricingInformation(ticker, ticker,
                        			new BigDecimal(parts[1]), null, new Date(), new BigDecimal(parts[2]),
                        			null, new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));

                        	Log.i(TAG, "Change: " + parts[6] + "," + parts[7]);
                        	float priceChange = Float.parseFloat(parts[6]);
                        	//                        companyDetail.setTodaysPriceChange(priceChange);
                        	StringBuffer s = new StringBuffer(parts[7].replace("\"", ""));

                        	s.setLength(s.length() - 1); // chop last char: %
                        	Log.i(TAG, "Percent: " + s);

                        	float percentChange = Float.parseFloat(s.toString());
                        	//                        Log.i(TAG, "Object not NULL?" + information);
                        	information.setPriceChange(priceChange);
                        	information.setPercentChange(percentChange);

                        	setOfEquityPricingInformation.add(information);
                        }
                    }
                    Log.i(TAG, "Notifying everyone");
                    notifyObservers(setOfEquityPricingInformation);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to get stock quotes", e);
                }
            }
        }, 1000, 10000);
    }


    /*
    * Get Company information for the Company Detail page
    * Yahoo fields:
    *    http://www.seangw.com/wordpress/index.php/2010/01/formatting-stock-data-from-yahoo-finance/
    *
    */
    public CompanyDetail getCompanyDetail(String aTicker) {

        String url = "http://download.finance.yahoo.com/d/quotes.csv?s=" + aTicker + "&f=nl1d1t1c1p2vem3m4xj1";
        System.out.println(url);
        CompanyDetail companyDetail = new CompanyDetail(aTicker);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int i = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (String s : parts) {
                    Log.i(TAG, s);
                }
                String name = parts[i].replace("\"", "");
                companyDetail.setName(name);
                i++;

                float lastPrice = Float.parseFloat(parts[i]);
                companyDetail.setPrice(lastPrice);

                float priceChange = Float.parseFloat(parts[4]);
                companyDetail.setTodaysPriceChange(priceChange);
                StringBuffer s = new StringBuffer(parts[5].replace("\"", ""));

                s.setLength(s.length() - 1); // chop last char: %
                Log.i(TAG, "Percent: " + s);
                float percentChange = Float.parseFloat(s.toString());
                companyDetail.setTodaysPercentChange(percentChange);
                long volume = Integer.parseInt(parts[6]);
                companyDetail.setVolume(volume);
                //
                float pe = Float.parseFloat(parts[7]);
                companyDetail.setPeRatio(pe);

                float mvAvg50 = Float.parseFloat(parts[8]);
                companyDetail.setMavg50(mvAvg50);

                float mvAvg200 = Float.parseFloat(parts[9]);
                companyDetail.setMavg200(mvAvg200);

                companyDetail.setExchange(parts[10]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, companyDetail.toString());

        return companyDetail;
    }

    /*
    *
    *
    */
    public List<EquityTimeSeries> getPriceHistoryForTicker(String aTicker) {
        List<EquityTimeSeries> aList = new ArrayList<EquityTimeSeries>();

        String url = "http://ichart.finance.yahoo.com/table.csv?s=" + aTicker + "&a=5&b=15&c=2010&d=05&e=29&f=2010&g=d";
        System.out.println(url);

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            //
            // Loop over Yahoo response and extract pricing information

            line = reader.readLine(); // skip first line

            while ((line = reader.readLine()) != null) {

                // AAPL",245.29,245.10,124.55,272.46,+12.76
                String[] parts = line.split(",");
//                System.out.println(parts[0] + "," + parts[1]);
                double open = Double.parseDouble(parts[1]);
                double high = Double.parseDouble(parts[2]);
                double low = Double.parseDouble(parts[3]);
                double close = Double.parseDouble(parts[4]);
                int volume = Integer.parseInt(parts[5]);

                aList.add(new EquityTimeSeries(parts[0], open, high, low, close, volume));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return aList;
    }
    
    /*
    *
    *
    */
    public static List<EquityTimeSeries> getPriceHistoryForTickerList(String aTicker) {
        List<EquityTimeSeries> aList = new ArrayList<EquityTimeSeries>();
        String url = "http://ichart.finance.yahoo.com/table.csv?s=" + aTicker + "&a=5&b=15&c=2010&d=06&e=29&f=2010&g=d";
        System.out.println(url);

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            //
            // Loop over Yahoo response and extract pricing information

            line = reader.readLine(); // skip first line

            while ((line = reader.readLine()) != null) {

                // AAPL",245.29,245.10,124.55,272.46,+12.76
                String[] parts = line.split(",");
//                System.out.println(parts[0] + "," + parts[1]);
                double open = Double.parseDouble(parts[1]);
                double high = Double.parseDouble(parts[2]);
                double low = Double.parseDouble(parts[3]);
                double close = Double.parseDouble(parts[4]);
                int volume = Integer.parseInt(parts[5]);

                aList.add(new EquityTimeSeries(parts[0], open, high, low, close, volume));
              

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(aList);
        return aList;
    }

    /*
    *
    *
    */
    public static List<EquityTimeSeries> getCandleStickDataForTicker(String aTicker) {
        List<EquityTimeSeries> aList = new ArrayList<EquityTimeSeries>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,-1 );
        int year1 = cal.get(Calendar.YEAR);  
        int month1 = cal.get(Calendar.MONTH);
        int day1 = cal.get(Calendar.DAY_OF_MONTH); 
        String reqStr = "&a="+month1+"&b="+day1+"&c="+year1;
        String url = "http://ichart.finance.yahoo.com/table.csv?s=" + aTicker + reqStr;
        //"&a=6&b=18&c=2010&d=07&e=18&f=2010&g=d";
        System.out.println(url);

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            //
            // Loop over Yahoo response and extract pricing information

            line = reader.readLine(); // skip first line

            while ((line = reader.readLine()) != null) {

                // AAPL",245.29,245.10,124.55,272.46,+12.76
                String[] parts = line.split(",");
//                System.out.println(parts[0] + "," + parts[1]);
                double open = Double.parseDouble(parts[1]);
                double high = Double.parseDouble(parts[2]);
                double low = Double.parseDouble(parts[3]);
                double close = Double.parseDouble(parts[4]);
                int volume = Integer.parseInt(parts[5]);

                aList.add(new EquityTimeSeries(parts[0], open, high, low, close, volume));
              

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(aList);
        return aList;
    }

    
    /*
     * Helper routine for getPriceHistoryForTicker()
     */
    public void showPrices(List<EquityTimeSeries> aList) {

        for (EquityTimeSeries ts : aList) {
            ts.dumpData();
        }
    }

    // =========
}
