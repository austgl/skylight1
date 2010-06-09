package skylight1.marketapp;

import android.os.Bundle;
import roboguice.activity.GuiceActivity;

public class AddWatchListTickerActivity extends GuiceActivity {
	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.add_watch_list_ticker);
	}
}
