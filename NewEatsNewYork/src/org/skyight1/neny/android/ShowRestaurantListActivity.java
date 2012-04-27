package org.skyight1.neny.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowRestaurantListActivity extends ListActivity {
	private class GetNewRestaurantsTask extends AsyncTask<String, Integer, List<String>> {
		protected List<String> doInBackground(String... urls) {
			final List<String> result = new ArrayList<String>();
			try {
				final URL url = new URL("http://neweatsnewyork.appspot.com/newRestaurants");
				final StringBuilder stringBuilder = new StringBuilder();
				final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				try {

					final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

					String line;
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
					}

					final JSONTokener tokener = new JSONTokener(stringBuilder.toString());
					final JSONArray arrayOfRestaurants = (JSONArray) tokener.nextValue();
					for (int i = 0; i < arrayOfRestaurants.length(); i++) {
						final JSONObject restaurant = arrayOfRestaurants.getJSONObject(i);
						final String restaurantName = restaurant.getString("doingBusinessAs");
						result.add(restaurantName);
					}
				} finally {
					urlConnection.disconnect();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		protected void onPostExecute(List<String> result) {
			ListView restaurantsListView = (ListView) findViewById(android.R.id.list);
			restaurantsListView.setAdapter(new ArrayAdapter<String>(ShowRestaurantListActivity.this, android.R.layout.simple_list_item_1, result));
		}
	}

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.restaurant_list_view);

		new GetNewRestaurantsTask().execute((String[]) null);
	}
}
