package org.skyight1.neny.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.skyight1.neny.android.database.RestaurantDatabase;
import org.skyight1.neny.android.database.model.Restaurant;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowRestaurantListActivity extends ListActivity {
			
	private class LoadLocalRestaurantsTask extends AsyncTask<String, Integer, List<String>> {
		
		protected List<String> doInBackground(String... unused) {
			
			final List<String> result = new ArrayList<String>();
			
			try {
				
				List<Restaurant> restaurants  = new RestaurantDatabase(ShowRestaurantListActivity.this).getRestaurants();
				
				Iterator<Restaurant> iter = restaurants.iterator();
				
				while (iter.hasNext()) {
					result.add(iter.next().getDoingBusinessAs());
				}
				
			} catch (Exception e) {
				
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

		new LoadLocalRestaurantsTask().execute((String[]) null);
	}
		
}
