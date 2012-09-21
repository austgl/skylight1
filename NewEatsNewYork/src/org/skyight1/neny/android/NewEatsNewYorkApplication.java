package org.skyight1.neny.android;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NewEatsNewYorkApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		Intent alarmIntent = new Intent(this, RestaurantDataDownloadReceiver.class);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		// ONCE A DAY
		//int REPEAT_CYCLE = 1000 * 60 * 60 * 24;
		
		// ONCE A MINUTE
		int REPEAT_CYCLE = 1000 * 60;
		
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, REPEAT_CYCLE, pendingIntent);
		
		// final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// final Intent intent = new Intent(this, RestaurantNotifier.class);
		// final PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);
		// alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, 60000, broadcast);

		// final Intent refreshIntent = new Intent(this, RefreshDatabaseService.class);
		// final PendingIntent refreshPendingIntent = PendingIntent.getService(this, 0, refreshIntent, 0);
		// alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, refreshPendingIntent);
	}
}
