package skylight1.marketapp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import skylight1.marketapp.feed.DummyEquityPricingInformationFeed;
import skylight1.marketapp.feed.EquityFeedObserver;
import skylight1.marketapp.feed.EquityPricingInformationFeed;
import skylight1.marketapp.model.EquityPricingInformation;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WatchListActivity extends Activity {
	// TODO inject this!!!!
	private EquityPricingInformationFeed equityPricingInformationFeed;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watchlist);

		equityPricingInformationFeed = new DummyEquityPricingInformationFeed();

		EquityFeedObserver equityFeedObserver = new EquityFeedObserver() {

			@Override
			public void equityPricingInformationUpdate(final Set<EquityPricingInformation> aSetOfEquityPricingInformation) {
				// TODO update my list
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.i(WatchListActivity.class.getName(), "here I am!!!");
						EquityPricingInformation equityPricingInformation = aSetOfEquityPricingInformation.iterator().next();
						Log.i(WatchListActivity.class.getName(), equityPricingInformation.getTicker());
						try {
							((TextView) findViewById(R.id.tempTickerName)).setText(String
									.format("%s = %f", equityPricingInformation.getTicker(), equityPricingInformation.getLastPrice()));
						} catch (Exception e) {
							Log.i(WatchListActivity.class.getName(), "exception!", e);
						}
					}
				});
			}
		};

		equityPricingInformationFeed.addEquityFeedObserver(equityFeedObserver, new HashSet<String>(Arrays.asList(new String[] { "INTC" })));
	}
}