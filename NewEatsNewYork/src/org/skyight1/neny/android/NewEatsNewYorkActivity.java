package org.skyight1.neny.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NewEatsNewYorkActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_view);

	}

	public void showRestaurants(final View aButton) {
		final Intent showRestaurantsIntent = new Intent(this, ShowRestaurantListActivity.class);
		startActivity(showRestaurantsIntent);
	}
}