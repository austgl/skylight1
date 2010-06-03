package skylight1.marketapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import skylight1.marketapp.feed.EquityFeedObserver;
import skylight1.marketapp.feed.EquityPricingInformationFeed;
import skylight1.marketapp.model.EquityPricingInformation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

public class WatchListActivity extends GuiceActivity {
	@Inject
	private EquityPricingInformationFeed equityPricingInformationFeed;

	@InjectView(R.id.tempTickerName)
	private TextView textView;
ListView  dbView;
ArrayAdapter<EquityPricingInformation> aa;
ArrayList<EquityPricingInformation>  MarketTable=new ArrayList<EquityPricingInformation>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watchlist);
		dbView=(ListView)this.findViewById(R.layout.epidb);
		int layoutID=android.R.layout.simple_list_item_1;
		aa=new ArrayAdapter<EquityPricingInformation>(this,layoutID,MarketTable);
		dbView.setAdapter(aa);
		
		

//		equityPricingInformationFeed = new DummyEquityPricingInformationFeed();

		EquityFeedObserver equityFeedObserver = new EquityFeedObserver() {

			@Override
			public void equityPricingInformationUpdate(final Set<EquityPricingInformation> aSetOfEquityPricingInformation) {
				// TODO update my list
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.i(WatchListActivity.class.getName(), "Updating watchList UI on UI Thread!!!");
						EquityPricingInformation equityPricingInformation = aSetOfEquityPricingInformation.iterator().next();
						Log.i(WatchListActivity.class.getName(), equityPricingInformation.getTicker());
						try {
							textView.setText(String.format("%s = %f", equityPricingInformation.getTicker(), equityPricingInformation.getLastPrice()));
						} catch (Exception e) {
							Log.i(WatchListActivity.class.getName(), "exception!", e);
						}
					}
				});
			}
		};

		equityPricingInformationFeed.addEquityFeedObserver(equityFeedObserver, new HashSet<String>(Arrays.asList(new String[] { "INTC" })));
		
	}
	private void addEquityPricingInformation(EquityPricingInformation epi){
		ContentResolver cr=getContentResolver();
		String w=dbMarket.KEY_SYMBOL+"="+epi.getTicker();
	Cursor c=	cr.query(dbMarket.CONTENT_URI, null, w, null, null)	;
	int dbCount=c.getCount();
	c.close();
	if (dbCount>0){
		ContentValues values=new ContentValues();
		values.put(dbMarket.KEY_SYMBOL, epi.getTicker());
		cr.insert(dbMarket.CONTENT_URI, values);
		MarketTable.add(epi);
		//Notify the array adapter of a change.
		aa.notifyDataSetChanged();
	}
	
}
                               }//end of class
	