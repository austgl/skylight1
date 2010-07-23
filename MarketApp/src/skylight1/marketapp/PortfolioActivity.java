package skylight1.marketapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.inject.Inject;
import roboguice.activity.GuiceListActivity;
import skylight1.marketapp.feed.EquityFeedObserver;
import skylight1.marketapp.feed.EquityPricingInformationFeed;
import com.admob.android.ads.AdManager;
import com.adwhirl.AdWhirlLayout;

import skylight1.marketapp.feed.YahooEquityPricingInformationFeed;
import skylight1.marketapp.model.CompanyDetail;
import skylight1.marketapp.model.EquityPricingInformation;

import java.util.*;


/**
 * Created by IntelliJ IDEA. User: melling Date: May 20, 2010 Time: 7:57:45 PM
 */
public class PortfolioActivity extends GuiceListActivity {

    private static final String TAG = PortfolioActivity.class.getSimpleName();
    public static final String ITEM_ID = "id";
    public static final String TICKER = "ticker";
    public static final String PRICE = "price";
    public static final String ASKSIZE = "askSize";
    public static final String TODAYSPRICECHANGE = "todaysPriceChange";
    public static final String TODAYPERCENTCHANGE = "todaysPercentChange";
    public static final String VOLUME = "volume";
    public static final String EXCHANGE = "exchange";
    public static final String EBITDA = "ebitda";
    public static final String PEGRATIO = "pegRatio";
    public static final String MAVG50 = "mavg50";
    public static final String MAVG200 = "mavg200";
    public static final String PERATIO = "peRatio";
    public static final String BIDSIZE = "bidSize";
    public static final String NAME = "name";

    private Map<String, EquityPricingInformation> tickerToEquityPricingInformationMap = new HashMap<String, EquityPricingInformation>();
    private Map<String, PortfolioItem> portfolioItemTickerMap = new HashMap<String, PortfolioItem>();

    Set<String> portfolioTickerSet;
    EquityFeedObserver equityFeedObserver;

    @Inject
    public EquityPricingInformationFeed equityPricingInformationFeed;

    private MarketDatabase marketDatabase;

    private EfficientAdapter aa;
    
    private static Hashtable <String,String> ht = new Hashtable();

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Portfolio Item Menu");
        menu.add(1, 1, 1, "Delete Ticker");
        menu.add(2, 2, 2, "Company Detail");
        menu.add(3, 3, 3, "Candlesticks");
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
                  MarketDatabase.PORTFOLIO_TABLE, context);
          marketDatabase2.cleanup();
          this.onResume();
          return true;
      }
      case 2:{
    	  onListItemClick(null, v, l.intValue(), -1);
    	  return true;
      }
      case 3:{    	  
    	  //will start receive ticker after CandleSticksActivity set  a to start receiving tickets
    	  Intent candlesticks = new Intent(this, CandleSticksActivity.class);
          candlesticks.putExtra(TICKER, (String) ht.get(Long.toString(info.id).trim()));
          startActivity(candlesticks);
          return true;
      }
        

      default:
        return super.onContextItemSelected(item);
      }
    } 

    static List<PortfolioItem> portfolioItems = new ArrayList<PortfolioItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio);
        Log.i(TAG, "Fetching prices");
//        initPortfolioList();
        registerForContextMenu(getListView());
        marketDatabase = new MarketDatabase(this);
        aa = new EfficientAdapter(this);
        setListAdapter(aa);
//        RelativeLayout aLayout = (RelativeLayout) getResources().getLayout(R.layout.portfolio);
//        AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, "fb61380198e84b59b03c46cdf49b7a0b");
//    	RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(320, 52);
//    	aLayout.addView(adWhirlLayout, adWhirlLayoutParams);
    	
    	try{
    		//admob: don't show ads in emulator
            AdManager.setTestDevices( new String[] { AdManager.TEST_EMULATOR
            //,"your_debugging_phone_id_here" // add phone id if debugging on phone
            });
            
            String adwhirl_id = getResources().getString(R.string.adwhirl_id);
            if(adwhirl_id!=null && adwhirl_id.length()>0) {
	            LinearLayout layout = (LinearLayout)findViewById(R.id.layout_ad);
	            AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, adwhirl_id);
	            Display d = this.getWindowManager().getDefaultDisplay();
	            layout.addView(adWhirlLayout);
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to create AdWhirlLayout", e);
        }
    }        

    private static class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private String dbId;
        private String tickerMsg;

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
         * sufficient to get at the data. If we were using a more complex data
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
                holder.currentPnlTextView = (TextView) convertView
                        .findViewById(R.id.positionPnl);

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
            holder.currentPnlTextView.setText("" + item.getPnL());
            dbId = item.getId();
            ht.put(Integer.toString(position),dbId);
            
        //    convertView.setOnTouchListener(this);
            tickerMsg = item.getTicker();
            return convertView;
        }


        static class ViewHolder {
            TextView tickerTextView;
            TextView avgPriceTextView;
            TextView numberOfSharesTextView;
            TextView currentPriceTextView;
            TextView currentPnlTextView;
        }


    }

    /*
    * Get tickers from Juan's DB.
    */

    private Set<PortfolioItem> loadPositionsFromMarketDB() {
        Set<PortfolioItem> tickerSet = new HashSet<PortfolioItem>();
        marketDatabase.open();
        Log.i(TAG, "Getting Position tickers");
        PortfolioItem portfolioItem;

        Cursor tickerCursor = marketDatabase.getAllPositions();
        startManagingCursor(tickerCursor); // Prevents error: "Finalizing a Cursor that has not been deactivated or closed"

        while (tickerCursor.moveToNext()) {
            int id = tickerCursor.getInt(0);
            String ticker = tickerCursor.getString(1);
            int quantity = tickerCursor.getInt(2);
            float avgPrice = tickerCursor.getFloat(3);
            Log.i(TAG, "Ticker: " + id + "==> " + ticker + ", nShares=" + quantity + ", AvgPrc=" + avgPrice);
            portfolioItem = new PortfolioItem(ticker, avgPrice, quantity, avgPrice);
            tickerSet.add(portfolioItem);
        }
        marketDatabase.cleanup();
        return tickerSet;
    }

    @Override
    public void onPause() {
        super.onPause();
//        final Set<String> watchListTickers = marketDatabase.getWatchListTickers();

        equityPricingInformationFeed.removeEquityForObserver(equityFeedObserver, portfolioTickerSet);
        Log.i(TAG, "Removing tickers");
    }
    /*
    * Get tickers from Juan's DB.
    */

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        portfolioItems.clear();
        final Set<PortfolioItem> positions = loadPositionsFromMarketDB();


        // Calculate P&L
        double pnl = 0f;
        double portfolioValue = 0;

        for (PortfolioItem position : positions) {

            pnl += position.getPnL();
            portfolioValue += position.getMarktetValue();
        }
        Log.i(TAG, pnl + "," + portfolioValue);

        TextView pnlText = (TextView)findViewById(R.id.TextView01);
        pnlText.setText("PNL: "+pnl); //TODO move label to resource
        
        final SortedSet<PortfolioItem> sortedPositions = new TreeSet<PortfolioItem>(positions);
        portfolioTickerSet = new TreeSet<String>();

        for (PortfolioItem position : sortedPositions) {
            PortfolioItem p;
            portfolioTickerSet.add(position.getTicker());
            p = new PortfolioItem(position.getTicker(),
                    position.getAveragePrice(),
                    position.getNumberOfShares(),
                    position.getAveragePrice(), position.getTicker());
            portfolioItems.add(p);
            portfolioItemTickerMap.put(position.getTicker(), p);
        }
        aa.notifyDataSetChanged();


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
                        Log.i(TAG, "Updating portfolio UI on UI Thread");
                        SortedSet<EquityPricingInformation> sortedPrices = new TreeSet<EquityPricingInformation>(aSetOfEquityPricingInformation);
                        for (EquityPricingInformation equityPricingInformation : sortedPrices) {
                            final String ticker = equityPricingInformation.getTicker();
                            Log.i(TAG, "Displaying PortfolioItem: " + ticker);

                            if (tickerToEquityPricingInformationMap.containsKey(ticker)) {
                                PortfolioItem p = portfolioItemTickerMap.get(ticker);
                                p.setCurrentPrice((equityPricingInformation.getLastPrice().floatValue()));
                                // Get Portfolio item and update latest price
//                                 aa.remove();
                            }

//                            aa.add(equityPricingInformation);
                            tickerToEquityPricingInformationMap.put(ticker, equityPricingInformation);
//                            addEquityPricingInformation(equityPricingInformation);
                        }
                        aa.notifyDataSetChanged();  // Let view know data set has changed - TODO: Should this be inside loop?
                    }
                });
            }
        };

        equityPricingInformationFeed.addEquityFeedObserver(equityFeedObserver, portfolioTickerSet);
    }


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

        Intent tickerinfo = new Intent(this, CompanyDetailActivity.class);
        
        tickerinfo.putExtra(ITEM_ID, position);
        tickerinfo.putExtra(TICKER, cd.getTicker());
        tickerinfo.putExtra(NAME, cd.getName());
        tickerinfo.putExtra(PRICE, Float.toString(cd.getPrice()));
        tickerinfo.putExtra(ASKSIZE, cd.getAskSize());
        tickerinfo.putExtra(TODAYSPRICECHANGE, Float.toString(cd.getTodaysPriceChange()));
        tickerinfo.putExtra(TODAYPERCENTCHANGE, Float.toString(cd.getTodaysPercentChange()));
        tickerinfo.putExtra(VOLUME, Long.toString(cd.getVolume()));
        tickerinfo.putExtra(EXCHANGE, cd.getExchange());
        tickerinfo.putExtra(MAVG50, Float.toString(cd.getMavg50()));
        tickerinfo.putExtra(MAVG200, Float.toString(cd.getMavg200()));
        tickerinfo.putExtra(EBITDA, cd.getEbita());
        tickerinfo.putExtra(PEGRATIO, cd.getPegRatio());
        tickerinfo.putExtra(PERATIO, Float.toString(cd.getPeRatio()));
        tickerinfo.putExtra(BIDSIZE, cd.getBidSize());

        startActivity(tickerinfo);
      //  sendBroadcast(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.portfolio_menu, menu);
        return true;
    }

}
