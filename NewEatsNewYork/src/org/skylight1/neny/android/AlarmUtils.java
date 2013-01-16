package org.skylight1.neny.android;

import static android.app.AlarmManager.INTERVAL_DAY;
import static android.app.AlarmManager.RTC_WAKEUP;
import static android.content.Context.ALARM_SERVICE;
import static org.skylight1.neny.android.database.model.MealTime.DINNER;
import static org.skylight1.neny.android.database.model.MealTime.LUNCH;

import java.util.Calendar;

import org.skylight1.neny.android.database.model.MealTime;
import org.skylight1.neny.android.notification.RestaurantNotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmUtils {
	public static final String MEAL_TIME_EXTRA_NAME = "mealTime";

	public void setAlarms(Context aContext) {
		final AlarmManager alarmManager = (AlarmManager) aContext.getSystemService(ALARM_SERVICE);

		final Intent alarmIntent = new Intent(aContext, RestaurantDataDownloadReceiver.class);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(aContext, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// TODO make this at midnight, and if no network then, then register for network state change
		alarmManager.setInexactRepeating(RTC_WAKEUP, System.currentTimeMillis(), INTERVAL_DAY, pendingIntent);

		setAlarmForHour(alarmManager, 11, LUNCH, aContext);
		setAlarmForHour(alarmManager, 17, DINNER, aContext);
	}

	private void setAlarmForHour(final AlarmManager anAlarmManager, int anHour, MealTime aMealTime, Context aContext) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, anHour);
		if (calendar.before(Calendar.getInstance())) {
			calendar.add(Calendar.DATE, 1);
		}
		final Intent intent = new Intent(aContext, RestaurantNotifier.class);
		intent.setAction(aMealTime.name());
		final PendingIntent broadcast = PendingIntent.getBroadcast(aContext, 0, intent, 0);
		anAlarmManager.setRepeating(RTC_WAKEUP, calendar.getTime().getTime(), INTERVAL_DAY, broadcast);
	}
}
