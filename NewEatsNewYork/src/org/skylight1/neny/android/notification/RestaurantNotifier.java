package org.skylight1.neny.android.notification;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.skylight1.neny.android.R;
import org.skylight1.neny.android.ShowRestaurantDetailActivity;
import org.skylight1.neny.android.database.RestaurantDatabase;
import org.skylight1.neny.android.database.dao.CuisinePreferences;
import org.skylight1.neny.android.database.dao.MealTimePreferences;
import org.skylight1.neny.android.database.dao.NeighborhoodPreferences;
import org.skylight1.neny.android.database.dao.PreferencesDao;
import org.skylight1.neny.android.database.model.Cuisine;
import org.skylight1.neny.android.database.model.DayAndTime;
import org.skylight1.neny.android.database.model.MealTime;
import org.skylight1.neny.android.database.model.Neighborhood;
import org.skylight1.neny.android.database.model.Restaurant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class RestaurantNotifier extends BroadcastReceiver {

	@Override
	public void onReceive(final Context aContext, final Intent anIntent) {
		final PowerManager powerManager = (PowerManager) aContext.getSystemService(POWER_SERVICE);
		final WakeLock wakeLock = powerManager.newWakeLock(SCREEN_BRIGHT_WAKE_LOCK, "yourprojectname");
		wakeLock.acquire();

		new Thread(new Runnable() {
			public void run() {
				try {
					notifyUserIfPreferredAndMatching(aContext, anIntent);
				} finally {
					wakeLock.release();
				}
			}
		}).start();
	}

	private void notifyUserIfPreferredAndMatching(Context aContext, Intent anIntent) {
		// check to see if the user wants a recommendation now
		final PreferencesDao mealTimePreferences = new MealTimePreferences(aContext);
		final int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		final MealTime mealTime = MealTime.valueOf(anIntent.getAction());
		final String preferenceName = DayAndTime.findByCode(day, mealTime).name();
		if (mealTimePreferences.getPreference(preferenceName, false)) {
			// find the restaurants that match the user's preferences
			final List<Neighborhood> neighborhoods = new NeighborhoodPreferences(aContext).getAllUserSelectedNeighborhoods();
			final List<Cuisine> cuisines = new CuisinePreferences(aContext).getAllUserSelectedCuisines();
			final ArrayList<Restaurant> listOfRestaurants = new RestaurantDatabase(aContext).getRestaurantsByUserPrefs(neighborhoods, cuisines);

			// if there is at least one restaurant
			if (!listOfRestaurants.isEmpty()) {
				Collections.shuffle(listOfRestaurants);
				final Restaurant restaurant = listOfRestaurants.get(0);

				// notify the user
				final NotificationManager notificationManager = (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
				final Notification notification = new Notification(R.drawable.nyne_logo2, restaurant.getDoingBusinessAs(), System.currentTimeMillis());
				final Intent showRestaurantDetailIntent = new Intent(aContext, ShowRestaurantDetailActivity.class);
				showRestaurantDetailIntent.putExtra("camis", restaurant.getCamis());
				final PendingIntent pendingIntent = PendingIntent.getActivity(aContext, 0, showRestaurantDetailIntent, 0);
				notification.setLatestEventInfo(aContext, restaurant.getDoingBusinessAs(), restaurant.getDoingBusinessAs(), pendingIntent);
				notificationManager.notify(1, notification);
			}
		}
	}
}
