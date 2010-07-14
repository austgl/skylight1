package skylight1.marketapp;

import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import roboguice.activity.GuiceListActivity;
import skylight1.marketapp.feed.EquityFeedObserver;
import skylight1.marketapp.feed.EquityPricingInformationFeed;
import skylight1.marketapp.feed.YahooEquityPricingInformationFeed;
import skylight1.marketapp.model.CompanyDetail;
import skylight1.marketapp.model.EquityPricingInformation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.inject.Inject;

public class WatchListActivity extends GuiceListActivity {

    EquityFeedObserver equityFeedObserver;
    private Set<String> watchListTickers;
    private static Hashtable <String,String> ht = new Hashtable();

    private static class EfficientAdapter extends ArrayAdapter<EquityPricingInformation> {
        private LayoutInflater mInflater;

        public EfficientAdapter(Context context, List<EquityPricingInformation> anEquityPricingInformationList) {

            super(context, R.layout.list_item_icon_text, anEquityPricingInformationList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

        }


        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.

            // When convertView is not null, we can reuse it directly, there is no need
            // to re-inflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_text, null);
            }

            TextView tickerTextView = (TextView) convertView.findViewById(R.id.ticker);
            tickerTextView.setTextColor(Color.BLACK);

            TextView avgPriceTextView = (TextView) convertView.findViewById(R.id.avgPrice);
            TextView numberOfSharesTextView = (TextView) convertView.findViewById(R.id.numberOfShares);
            numberOfSharesTextView.setTextColor(Color.BLACK);

            TextView currentPriceTextView = (TextView) convertView.findViewById(R.id.currentPrice);
            currentPriceTextView.setTextColor(Color.BLACK);

            // Bind the data efficiently with the holder.
            avgPriceTextView.setTextColor(Color.GREEN);
            final EquityPricingInformation item = getItem(position);
            tickerTextView.setText(item.getTicker());

            avgPriceTextView.setText(item.getLastPrice().toString());
            numberOfSharesTextView.setText(Float.toString(item.getPriceChange())); // TODO: format properly
            currentPriceTextView.setText(Float.toString(item.getPercentChange()) + "%"); // TODO: format properly
            ht.put(Integer.toString(position),item.getTicker());

            return convertView;
        }

    }

    private static final String TAG = WatchListActivity.class.getSimpleName();

    private Map<String, EquityPricingInformation> tickerToEquityPricingInformationMap = new HashMap<String, EquityPricingInformation>();

    @Inject
    public EquityPricingInformationFeed equityPricingInformationFeed;

//    @Inject
    private MarketDatabase marketDatabase;

    ListView dbView;
    ArrayAdapter<EquityPricingInformation> aa;
    ArrayList<EquityPricingInformation> marketTable = new ArrayList<EquityPricingInformation>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchlist);
        registerForContextMenu(getListView());

        dbView = (ListView) this.findViewById(R.layout.epidb);

        aa = new EfficientAdapter(this, new ArrayList<EquityPricingInformation>());

        setListAdapter(aa);


        // melling's DB

//        PortDbAdapter mDbHelper = new PortDbAdapter(this);
//        mDbHelper.open();
        marketDatabase = new MarketDatabase(this);
        marketDatabase.open();

    }

    /*
     *
     */


    @Override
    public void onPause() {
        super.onPause();
//        final Set<String> watchListTickers = marketDatabase.getWatchListTickers();

        equityPricingInformationFeed.removeEquityForObserver(equityFeedObserver, watchListTickers);
        Log.i(TAG, "Removing tickers");
    }


    /*
    * Get tickers from melling's DB.
    */
/*
    private Set<String> loadTickersFromDatabase() {
        Set<String> tickerSet = new HashSet<String>();

        mDbHelper.open();
        Cursor portfolioCursor = mDbHelper.fetchAllPorts();
        startManagingCursor(portfolioCursor);

        //   portfolioCursor.moveToFirst();
        while (portfolioCursor.moveToNext()) {
            int id = portfolioCursor.getInt(0);
            String ticker = portfolioCursor.getString(1);
            Log.i(TAG, id + "- " + ticker);
            tickerSet.add(ticker);
        }
        mDbHelper.close();

        return tickerSet;
    }
*/


    /*
     * Get tickers from Juan's DB.
     */

    private Set<String> loadTickersFromMarketDB() {
        Set<String> tickerSet = new HashSet<String>();
        Log.i(TAG, "Getting Watchlist tickers");

        Cursor tickerCursor = marketDatabase.getAllWatchlistTickers();
        startManagingCursor(tickerCursor); // Prevents error: "Finalizing a Cursor that has not been deactivated or closed"

        while (tickerCursor.moveToNext()) {
            int id = tickerCursor.getInt(0);
            String ticker = tickerCursor.getString(1);
            Log.i(TAG, "Ticker: " + id + "==> " + ticker);
            tickerSet.add(ticker);
        }
//        marketDatabase.cleanup();
        return tickerSet;
    }

    /*
    *
    */

    @Override
    public void onResume() {

        super.onResume();

        // First show the tickers and current prices... Uh, we have no prices yet.
        watchListTickers = loadTickersFromMarketDB();
        Log.i(TAG, "# Tickers: " + watchListTickers.size());
//        EquityPricingInformation eq = new EquityPricingInformation();
//
//        for (String ticker: watchListTickers) {
//            aa.add(ticker);
//        }
        equityFeedObserver = new EquityFeedObserver() {


            @Override
            public void equityPricingInformationUpdate(final Set<EquityPricingInformation> aSetOfEquityPricingInformation) {
                // TODO update my list
                runOnUiThread(new Runnable() {

                    /*
                     * We are going to be called with a prices for which we've subscribed.  Sorting the set for now to control the
                     * ordering.
                     * 
                     */
                    @Override
                    public void run() {
                        Log.i(TAG, "Updating watchList UI on UI Thread!!!");
                        SortedSet<EquityPricingInformation> sortedPrices = new TreeSet<EquityPricingInformation>(aSetOfEquityPricingInformation);
//                        for (EquityPricingInformation equityPricingInformation : aSetOfEquityPricingInformation) {
                        for (EquityPricingInformation equityPricingInformation : sortedPrices) {
                            final String ticker = equityPricingInformation.getTicker();
                            Log.i(TAG, "Displaying: " + ticker);
                            
                            if (tickerToEquityPricingInformationMap.containsKey(ticker)) {
                                aa.remove(tickerToEquityPricingInformationMap.get(ticker));
                            }

                            aa.add(equityPricingInformation);
                            aa.notifyDataSetChanged();  // Let view know data set has changed - TODO: Should this be inside loop?
                            tickerToEquityPricingInformationMap.put(ticker, equityPricingInformation);
                            addEquityPricingInformation(equityPricingInformation);
                        }
                    }
                });
            }
        };

//        final Set<String> watchListTickers = loadTickersFromDatabase();
//        final Set<String> watchListTickers2 = marketDatabase.getWatchListTickers();
//        final Set<String> watchListTickers = loadTickersFromMarketDB();
        equityPricingInformationFeed.addEquityFeedObserver(equityFeedObserver, watchListTickers);
    }


    /*
    * Add pricing information to the database
    */

    private void addEquityPricingInformation(EquityPricingInformation epi) {
        ContentResolver contentResolver = getContentResolver();
        String whereClause = MarketDatabase.KEY_SYMBOL + "='" + epi.getTicker() + "'";
        // Check to see if ticker exists
        Log.i(TAG, "where=" + whereClause);
        Cursor c = contentResolver.query(MarketDatabase.CONTENT_URI, null, whereClause, null, null);
        int dbCount;

        if (c != null) {
            dbCount = c.getCount();
            c.close();
        } else {
            dbCount = 0;
        }
        ContentValues values = new ContentValues();
        values.put(MarketDatabase.KEY_SYMBOL, epi.getTicker());

        if (dbCount > 0) { // Then ticker exists so update

            contentResolver.update(MarketDatabase.CONTENT_URI, values, whereClause, null);
            marketTable.add(epi);

        } else {
            contentResolver.insert(MarketDatabase.CONTENT_URI, values);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_watch_ticker) {
            Intent intent = new Intent(WatchListActivity.this, AddWatchListTickerActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.refresh_prices) {
            Log.i("FOO", "Clicked mid_OpenGL_AnimatedTriangle15 II");

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu
            menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.watch_list_menu, menu);

        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("WatchList Item Menu");
        menu.add(1, 1, 1, "Delete Ticker");
        menu.add(2, 2, 2, "Company Detail");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	final Context context = this.getBaseContext();
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      Long l = info.id;
      View v = info.targetView;
      switch (item.getItemId()) {
      case 1:{
    	  MarketDatabase marketDatabase2 = new MarketDatabase(context);
          String whereArgs[] = new String[1];
          whereArgs[0] = (String) ht.get(Long.toString(info.id).trim());
          marketDatabase2.open();
          marketDatabase2.delete(MarketDatabase.CONTENT_URI,
                  MarketDatabase.KEY_SYMBOL,
                  whereArgs,
                  MarketDatabase.WATCHLIST_TABLE, context);
          marketDatabase2.cleanup();
          return true;
      }
      case 2:{
    	  onListItemClick(null, v, l.intValue(), -1);
    	  return true;
      }
      case 3:
        return true;
      default:
        return super.onContextItemSelected(item);
      }
    } 
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	if(l != null && id > -1)
    	{
    		super.onListItemClick(l, v, position, id);
    	}
        TextView ticker = (TextView) v.findViewById(R.id.ticker);
        YahooEquityPricingInformationFeed ef = new YahooEquityPricingInformationFeed();

        CompanyDetail cd = ef.getCompanyDetail(ticker.getText().toString());

        Log.i(TAG, "CompanyDetail:" + cd.toString());

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        Editor edit = settings.edit();

        edit.putInt(PortfolioActivity.ITEM_ID, position);
        edit.putString(PortfolioActivity.TICKER, cd.getTicker());
        edit.putString(PortfolioActivity.NAME, cd.getName());
        edit.putString(PortfolioActivity.PRICE, Float.toString(cd.getPrice()));
        edit.putString(PortfolioActivity.ASKSIZE, cd.getAskSize());
        edit.putString(PortfolioActivity.TODAYSPRICECHANGE, Float.toString(cd.getTodaysPriceChange()));
        edit.putString(PortfolioActivity.TODAYPERCENTCHANGE, Float.toString(cd.getTodaysPercentChange()));
        edit.putString(PortfolioActivity.VOLUME, Long.toString(cd.getVolume()));
        edit.putString(PortfolioActivity.EXCHANGE, cd.getExchange());
        edit.putString(PortfolioActivity.MAVG50, Float.toString(cd.getMavg50()));
        edit.putString(PortfolioActivity.MAVG200, Float.toString(cd.getMavg200()));
        edit.putString(PortfolioActivity.EBITDA, cd.getEbita());
        edit.putString(PortfolioActivity.PEGRATIO, cd.getPegRatio());
        edit.putString(PortfolioActivity.PERATIO, Float.toString(cd.getPeRatio()));
        edit.putString(PortfolioActivity.BIDSIZE, cd.getBidSize());
        edit.commit();
        Intent i = new Intent(this, CompanyDetailActivity.class);
        startActivity(i);
      //  sendBroadcast(i);
    }
}	