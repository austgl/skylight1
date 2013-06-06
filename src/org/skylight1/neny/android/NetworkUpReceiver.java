package org.skylight1.neny.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class NetworkUpReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkUpReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences prefs = context.getSharedPreferences("get_restaurants_task_settings", Context.MODE_PRIVATE);
		if(prefs.getBoolean("need_to_getrestaurants", false)){
			Log.i(TAG, "Network up..Getting restaurants");
			
			new GetNewRestaurantsTask(context).execute((String[]) null);
			//Store the fact that we got the restaurants
			Editor edit = prefs.edit();
			edit.putBoolean("need_to_getrestaurants", false);
			edit.commit();
		}
		else{
			Log.i(TAG,"Looks like we got the restaurants previously.. So not firing the task");
		}
	}
}
