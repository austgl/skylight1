package skylight1.marketapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MarketAppHomeActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		findViewById(R.id.watchListButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				Intent intent = new Intent(MarketAppHomeActivity.this, WatchListActivity.class);
				startActivity(intent);
			}
		});
	}
}