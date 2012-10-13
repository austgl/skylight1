package org.skylight1.neny.android;

import java.util.ArrayList;

import org.skyight1.neny.android.database.model.Restaurant;
import org.skylight1.neny.android.database.RestaurantDatabase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ShowRestaurantListActivity extends ListActivity {
	
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	
	private class LoadLocalRestaurantsTask extends AsyncTask<String, Integer, String> {
		
		protected String doInBackground(String... unused) {
					
			String status = "retrieving restaurants from local database...";
			
			try {
				
				restaurants = new RestaurantDatabase(ShowRestaurantListActivity.this).getRestaurants();
				
				status = restaurants.size() + " restaurants retrieved";
				
			} catch (Exception e) {
				
				status = e.getMessage();
				e.printStackTrace();
				
				
			}
			
			return status;
		}
		
		// We don't need to pass this array around
		protected void onPostExecute(String result) {
			
			ListView restaurantsListView = (ListView) findViewById(android.R.id.list);
		
			restaurantsListView.setAdapter(new RestaurantArrayAdapter(ShowRestaurantListActivity.this, restaurants));
			
		}
		
	}
	
	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.restaurant_list_view);

		new LoadLocalRestaurantsTask().execute((String[]) null);
		
	}
	
	@Override
	protected void onListItemClick(ListView lv, View v, int position, long id) {
		
		Restaurant restaurant = restaurants.get(position);
		
		String camis = restaurant.getCamis();
		
		Intent intent = new Intent(this, ShowRestaurantDetailActivity.class);
		
		intent.putExtra("camis", camis);
		
		startActivity(intent);
		
	}
	
		
}
