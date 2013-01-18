package org.skylight1.neny.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class RestaurantDataDownloadReceiver extends BroadcastReceiver {

	private static final String TAG = RestaurantDataDownloadReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "onReceive");
		
		if (isNetworkAvailable(context)) {
			
			Log.i(TAG, "Getting new restaurant data");
		
            new GetNewRestaurantsTask(context).execute((String[]) null);
		} else {
			//TODO how do we unregister a receiver .. A receiver cannot unregister itself
			//FIXME as a temp workaround , storing in sharedprefs whether we need to send get list of restaurants
			Log.i(TAG, "No internet connection");
			Log.i(TAG,"Store fact that we need to request restaurants when network is up");
			SharedPreferences preferences = context.getSharedPreferences("get_restaurants_task_settings", Context.MODE_PRIVATE);
			Editor edit = preferences.edit();
			edit.putBoolean("need_to_getrestaurants", true);
			edit.commit();
			

		}
	}
	
	private boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}	

}
