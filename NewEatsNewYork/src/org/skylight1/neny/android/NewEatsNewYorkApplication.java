package org.skylight1.neny.android;

import java.util.Calendar;

import org.skylight1.neny.android.notification.RestaurantNotifier;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NewEatsNewYorkApplication extends Application {
	public static final String MEAL_TIME_EXTRA_NAME = "mealTime";
	public static final int LUNCH = 1;
	public static final int DINNER = 2;
	
	@Override
	public void onCreate() {
		super.onCreate();

		Intent alarmIntent = new Intent(this, RestaurantDataDownloadReceiver.class);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, AlarmManager.INTERVAL_DAY, pendingIntent);

		setAlarmForHour(alarmManager, 11, LUNCH);
		setAlarmForHour(alarmManager, 17, DINNER);
		
	}
	
	private void setAlarmForHour(final AlarmManager anAlarmManager, int anHour, int aMealTimeIndicator) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, anHour);
		if (calendar.before(Calendar.getInstance())) {
			calendar.add(Calendar.DATE, 1);
		}
		final long msToElevenAm = calendar.getTime().getTime() - System.currentTimeMillis();
		
		final Intent intent = new Intent(this, RestaurantNotifier.class);
		intent.putExtra(MEAL_TIME_EXTRA_NAME, aMealTimeIndicator);
		final PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);
		anAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, msToElevenAm, AlarmManager.INTERVAL_DAY, broadcast);
	}
}
