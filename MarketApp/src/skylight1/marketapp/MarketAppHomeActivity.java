package skylight1.marketapp;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MarketAppHomeActivity extends GuiceActivity {
	@InjectView(R.id.watchListButton)
	private Button watchListButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		watchListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View aV) {
				Intent intent = new Intent(MarketAppHomeActivity.this, WatchListActivity.class);
				startActivity(intent);
			}
		});
	}
}