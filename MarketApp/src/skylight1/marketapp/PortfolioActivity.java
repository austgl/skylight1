package skylight1.marketapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import skylight1.marketapp.feed.YahooEquityPricingInformationFeed;
import skylight1.marketapp.model.CompanyDetail;


/**
 * Created by IntelliJ IDEA. User: melling Date: May 20, 2010 Time: 7:57:45 PM
 */
public class PortfolioActivity extends ListActivity {

    private static final String TAG = PortfolioActivity.class.getSimpleName();
    public static final String ITEM_ID = "id";
    public static final String TICKER = "ticker";
    public static final String PRICE ="price";
    public static final String ASKSIZE = "askSize";
    public static final String TODAYSPRICECHANGE = "todaysPriceChange";
    public static final String TODAYPERCENTCHANGE = "todaysPercentChange";
    public static final String VOLUME ="volume";
    public static final String EXCHANGE = "exchange";
    public static final String EBITDA = "ebitda";
    public static final String PEGRATIO = "pegRatio";
    public static final String MAVG50 = "mavg50";
    public static final String MAVG200 = "mavg200";
    public static final String PERATIO ="peRatio";
    public static final String BIDSIZE ="bidSize";
    
    private MarketDatabase marketDatabase;

    private EfficientAdapter aa;
    
    private YahooEquityPricingInformationFeed ef;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Action 1");
        menu.add(0, v.getId(), 0, "Action 2");
        menu.add(0, v.getId(), 0, "Action 3");
    }

    // private static final String[] DATA = {"AAPL", "GOOG"};
    static List<PortfolioItem> portfolioItems = new ArrayList<PortfolioItem>();

    private static class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
        }

        /**
         * The number of items in the list is determined by the number of
         * speeches in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
            return portfolioItems.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid
            // unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is
            // no need
            // to reinflate it. We only inflate a new View when the convertView
            // supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_icon_text,
                        null);
                // Creates a ViewHolder and store references to the two children
                // views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.tickerTextView = (TextView) convertView
                        .findViewById(R.id.ticker);
                holder.avgPriceTextView = (TextView) convertView
                        .findViewById(R.id.avgPrice);
                holder.numberOfSharesTextView = (TextView) convertView
                        .findViewById(R.id.numberOfShares);
                holder.currentPriceTextView = (TextView) convertView
                        .findViewById(R.id.currentPrice);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();

            }

            // Bind the data efficiently with the holder.
            holder.avgPriceTextView.setTextColor(Color.GREEN);
            final PortfolioItem item = portfolioItems.get(position);
            holder.tickerTextView.setText(item.getTicker());
            holder.avgPriceTextView.setText(item.getAveragePriceStr());
            holder.numberOfSharesTextView
                    .setText(item.getNumberOfSharesAsStr());
            holder.currentPriceTextView.setText(item.getCurrentPriceStr());

            return convertView;
        }

        static class ViewHolder {
            TextView tickerTextView;
            TextView avgPriceTextView;
            TextView numberOfSharesTextView;
            TextView currentPriceTextView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // setContentView(R.layout.portfolio);
        Log.i(TAG, "Fetching prices");
//        initPortfolioList();
        marketDatabase = new MarketDatabase(this);
        aa = new EfficientAdapter(this);
        setListAdapter(aa);

    }


    /*
    * Get tickers from Juan's DB.
    */
    private Set<String> loadPositionsFromMarketDB() {
        Set<String> tickerSet = new HashSet<String>();
        marketDatabase.open();
        Log.i(TAG, "Getting Position tickers");

        Cursor tickerCursor = marketDatabase.getAllPositions();
        startManagingCursor(tickerCursor); // Prevents error: "Finalizing a Cursor that has not been deactivated or closed"
        
        while (tickerCursor.moveToNext()) {
            int id = tickerCursor.getInt(0);
            String ticker = tickerCursor.getString(1);
            Log.i(TAG, "Ticker: " + id + "==> " + ticker);
            tickerSet.add(ticker);
        }
        marketDatabase.cleanup();
        return tickerSet;
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");
        portfolioItems.clear();
        final Set<String> positions = loadPositionsFromMarketDB();
        for (String ticker: positions) {
            portfolioItems.add(new PortfolioItem(ticker, 202.0f, 1000, 250.10f));
        }
        aa.notifyDataSetChanged();
    }

/*
    private void initPortfolioList() {
        portfolioItems.add(new PortfolioItem("AAPL", 202.0f, 1000, 250.10f));
        portfolioItems.add(new PortfolioItem("GOOG", 20.0f, 10000, 50.10f));
        portfolioItems.add(new PortfolioItem("MSFT", 20.1f, 50000, 250.10f));
        portfolioItems.add(new PortfolioItem("IBM", 22.0f, 100000, 19.21f));
        portfolioItems.add(new PortfolioItem("C", 3.74f, 100, 3.78f));
        portfolioItems.add(new PortfolioItem("JDSU", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("ADBE", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("PALM", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("F", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("GM", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("JNPR", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("AMD", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("INTC", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("GE", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("RIMM", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("WEC", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("DELL", 10.78f, 9999, 12.54f));
        portfolioItems.add(new PortfolioItem("SY", 10.78f, 9999, 12.54f));
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_position) {
            Intent intent = new Intent(PortfolioActivity.this,
                    AddPortfolioItemActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.refresh_portfolio_prices) {
            Log.i(TAG, "Refreshing portfolio prices");

        } else if (item.getItemId() == R.id.Detail) {

            Log.i(TAG, "CompanyDetail");
            Intent intent = new Intent(this, CompanyDetailActivity0.class);
            startActivity(intent);

        }
        return true;
    }

    public static final String NAME="NAME";

    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView ticker = (TextView) v.findViewById(R.id.ticker);
        ef = new YahooEquityPricingInformationFeed();
        
    	CompanyDetail cd = ef.getCompanyDetail(ticker.getText().toString());    	
    	
    	Log.i(TAG, "CompanyDetail:"+cd.toString());
        
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        Editor edit = settings.edit();

        edit.putInt(ITEM_ID, position);
        edit.putString(TICKER, cd.getTicker());
        edit.putString(NAME,cd.getName());
        edit.putString(PRICE,Float.toString(cd.getPrice()));
        edit.putString(ASKSIZE, cd.getAskSize());
        edit.putString(TODAYSPRICECHANGE, Float.toString(cd.getTodaysPriceChange()));
        edit.putString(TODAYPERCENTCHANGE, Float.toString(cd.getTodaysPercentChange()));
        edit.putString(VOLUME, Long.toString(cd.getVolume()));
        edit.putString(EXCHANGE, cd.getExchange());
        edit.putString(MAVG50, Float.toString(cd.getMavg50()));
        edit.putString(MAVG200,Float.toString(cd.getMavg200() ));
        edit.putString(EBITDA, cd.getEbita());
        edit.putString(PEGRATIO, cd.getPegRatio());
        edit.putString(PERATIO,Float.toString(cd.getPeRatio()));
        edit.putString(BIDSIZE, cd.getBidSize());  
        edit.commit();   	
        Intent i = new Intent(this, CompanyDetailActivity.class);
        startActivity(i);
        sendBroadcast(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.portfolio_menu, menu);
        return true;
    }

}
