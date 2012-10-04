package org.skyight1.neny.android.notification;

import java.util.Calendar;

import org.skyight1.neny.android.NewEatsNewYorkApplication;
import org.skyight1.neny.android.R;
import org.skyight1.neny.android.ShowRestaurantListActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class RestaurantNotifier extends BroadcastReceiver {

	private SharedPreferences preferences;

	@Override
	public void onReceive(Context aContext, Intent anIntent) {
		final NotificationManager notificationManager = (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(R.drawable.nyne_logo2, "Feed Me!", System.currentTimeMillis());

		final Intent showRestaurantsIntent = new Intent(aContext, ShowRestaurantListActivity.class);
		final PendingIntent broadcast = PendingIntent.getActivity(aContext, 0, showRestaurantsIntent, 0);

		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		preferences = aContext.getSharedPreferences("dining_times",Context.MODE_PRIVATE);
		final int mealTime = anIntent.getIntExtra(NewEatsNewYorkApplication.MEAL_TIME_EXTRA_NAME, 0);
		if(preferences.getBoolean(String.valueOf(day)+String.valueOf(mealTime), false)){
			notification.setLatestEventInfo(aContext, "Feed me", "Feed me", broadcast);
			notificationManager.notify(1, notification);
		}
		
	}
}
