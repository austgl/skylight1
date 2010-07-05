package skylight1.marketapp;

import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import roboguice.activity.GuiceListActivity;
import skylight1.marketapp.feed.EquityFeedObserver;
import skylight1.marketapp.feed.EquityPricingInformationFeed;
import skylight1.marketapp.model.EquityPricingInformation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;
import skylight1.marketapp.model.PortDbAdapter;

public class WatchListActivity extends GuiceListActivity {

    EquityFeedObserver equityFeedObserver;
    private PortDbAdapter mDbHelper;

    private static class EfficientAdapter extends ArrayAdapter<EquityPricingInformation> {
        private LayoutInflater mInflater;


        public EfficientAdapter(Context context, List<EquityPricingInformation> anEquityPricingInformationList) {

            super(context, R.layout.list_item_icon_text, anEquityPricingInformationList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
        }


        /*   *//**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         *//*
        public int getCount() {
            return watchListItems.size();
        }

        *//**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         *//*
        public Object getItem(int position) {
            return position;
        }

        *//**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         *//*
        public long getItemId(int position) {
            return position;
        }
*/

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
            numberOfSharesTextView.setText("N/A");
            currentPriceTextView.setText("100.0");


            return convertView;
        }

/*
        static class ViewHolder {
            TextView tickerTextView;
            TextView avgPriceTextView;
            TextView numberOfSharesTextView;
            TextView currentPriceTextView;
        }
*/
    }

    private static final String TAG = WatchListActivity.class.getSimpleName();

    private Map<String, EquityPricingInformation> tickerToEquityPricingInformationMap = new HashMap<String, EquityPricingInformation>();

    @Inject
    public EquityPricingInformationFeed equityPricingInformationFeed;

//    @Inject
    public MarketDatabase marketDatabase;

//    @InjectView(R.id.tempTickerName) public TextView textView;

    ListView dbView;
//    ArrayAdapter<EquityPricingInformation> aa;
    ArrayAdapter<EquityPricingInformation> aa;
//    ListAdapter bb;
    ArrayList<EquityPricingInformation> MarketTable = new ArrayList<EquityPricingInformation>();
//    private static String[] tickerList = {"AAPL", "MSFT", "GOOG"};

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchlist);
//        EquityPricingInformation eq1 = new EquityPricingInformation("AAPL", "AAPL", new BigDecimal(50), new BigDecimal(100000), new Date(), new BigDecimal(40), new BigDecimal(120000), new Date());
//        MarketTable.add(eq1);
        dbView = (ListView) this.findViewById(R.layout.epidb);
//        int layoutID = android.R.layout.simple_list_item_1;
//        aa = new ArrayAdapter<EquityPricingInformation>(this, layoutID, tickerList);
//        Set<String> tickerList = marketDatabase.getWatchListTickers();

        aa = new EfficientAdapter(this, new ArrayList<EquityPricingInformation>());

        //  dbView.setAdapter(aa);
        setListAdapter(aa);


        // melling's DB

        mDbHelper = new PortDbAdapter(this);
//        mDbHelper.open();
        marketDatabase = new MarketDatabase();
    }

    /*
     *
     */


    @Override
    public void onPause() {
        super.onPause();
        final Set<String> watchListTickers = marketDatabase.getWatchListTickers();

        equityPricingInformationFeed.removeEquityForObserver(equityFeedObserver, watchListTickers);
        Log.i(TAG, "Removing tickers");
    }


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
    /*
    *
    */

    @Override
    public void onResume() {

        super.onResume();
        equityFeedObserver = new EquityFeedObserver() {


            @Override
            public void equityPricingInformationUpdate(final Set<EquityPricingInformation> aSetOfEquityPricingInformation) {
                // TODO update my list
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Updating watchList UI on UI Thread!!!");
                        for (EquityPricingInformation equityPricingInformation : aSetOfEquityPricingInformation) {
                            final String ticker = equityPricingInformation.getTicker();
                            Log.i(TAG, ticker);
                            if (tickerToEquityPricingInformationMap.containsKey(ticker)) {
                                aa.remove(tickerToEquityPricingInformationMap.get(ticker));
                            }

                            aa.add(equityPricingInformation);
                            aa.notifyDataSetChanged();  // Let view know data set has changed
                            tickerToEquityPricingInformationMap.put(ticker, equityPricingInformation);
                            addEquityPricingInformation(equityPricingInformation);
                        }
                    }
                });
            }
        };

        final Set<String> watchListTickers = loadTickersFromDatabase();
        final Set<String> watchListTickers2 = marketDatabase.getWatchListTickers();

        equityPricingInformationFeed.addEquityFeedObserver(equityFeedObserver,
                watchListTickers);
//        watchListTickers.
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
            MarketTable.add(epi);

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
}	