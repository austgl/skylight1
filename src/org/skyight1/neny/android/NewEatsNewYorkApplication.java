package org.skyight1.neny.android;

import org.skyight1.neny.android.notification.RestaurantNotifier;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

public class NewEatsNewYorkApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		final Intent intent = new Intent(this, RestaurantNotifier.class);
		final PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);

		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, 60000, broadcast);
	}
}
