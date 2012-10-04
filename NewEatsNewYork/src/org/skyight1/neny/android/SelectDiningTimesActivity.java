package org.skyight1.neny.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SelectDiningTimesActivity extends Activity{

	private static final String TAG ="SelectDiningTimesActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_view);
		Log.i(TAG, "Select Dining Activities");
	}
}
