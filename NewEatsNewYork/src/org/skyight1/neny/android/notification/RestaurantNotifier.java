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

public class RestaurantNotifier extends BroadcastReceiver {

	@Override
	public void onReceive(Context aContext, Intent aArg1) {
		final NotificationManager notificationManager = (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notification = new Notification(R.drawable.nyne_logo2, "Feed Me!", System.currentTimeMillis());

		final Intent showRestaurantsIntent = new Intent(aContext, ShowRestaurantListActivity.class);
		final PendingIntent broadcast = PendingIntent.getActivity(aContext, 0, showRestaurantsIntent, 0);

		notification.setLatestEventInfo(aContext, "Feed me", "Feed me", broadcast);
		notificationManager.notify(1, notification);
	}
}
