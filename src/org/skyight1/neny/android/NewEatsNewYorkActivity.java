package org.skyight1.neny.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.skyight1.neny.android.database.RestaurantDatabase;
import org.skyight1.neny.android.database.model.Address;
import org.skyight1.neny.android.database.model.Borough;
import org.skyight1.neny.android.database.model.Grade;
import org.skyight1.neny.android.database.model.Restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NewEatsNewYorkActivity extends Activity {

	private RestaurantDatabase restaurantDatabase = new RestaurantDatabase(this);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_view);
	}
	
	@Override
	public void onResume() {
		
		super.onResume();		
		refreshLastUpdateDate();
	}
	
	private void refreshLastUpdateDate() {		
		new GetLastUpdateTask().execute((String[]) null);
	}
	
	public void showRestaurants(final View aButton) {
		final Intent showRestaurantsIntent = new Intent(this, ShowRestaurantListActivity.class);

		startActivity(showRestaurantsIntent);
	}

	
	public void selectDiningTimes(final View aButton) {
		final Intent showTimeIntent = new Intent(this, SelectDiningTimesActivity.class);

		startActivity(showTimeIntent);
	}

	public void selectNeighborhoods(final View aButton) {
		final Intent showTimeIntent = new Intent(this, SelectNeighborhoodsActivity.class);

		startActivity(showTimeIntent);
	}

	public void selectCuisines(final View aButton) {
		final Intent showTimeIntent = new Intent(this, SelectCuisinesActivity.class);

		startActivity(showTimeIntent);
	}

	
	public void updateRestaurantList(final View aButton) {
		
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		
		adb.setTitle("Update Restaurants from Server")
		.setMessage("It may take a few  minutes to refresh the restaurant list.  Continue?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startDownload();
			}
		})
		.create()
		.show();
		
	}
		
	private void startDownload() {
		
		new GetNewRestaurantsTask().execute((String[]) null);
		
	}
	
	private class GetNewRestaurantsTask extends AsyncTask<String, Integer, String> {
					
		private List<Restaurant> aRestaurants = new ArrayList<Restaurant>();
		ProgressDialog dialog;
		
		protected void onPreExecute() {
			
			dialog = new ProgressDialog(NewEatsNewYorkActivity.this);
			dialog.setTitle("Retrieving latest restaurant list...");
			dialog.show();
			
		}
		
		protected String doInBackground(String... urls) {
			
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
			final List<String> result = new ArrayList<String>();
			
		    String status = "Connecting...";
			
			try {

				final URL url = new URL(getResources().getString(R.string.server));
				
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
						
						// camis
						// doingBusinessAs
						// borough
						
						/*  part of address object */
						// building
						// street
						// zipCode
						
						// phone
						// cuisineCode
						// currentGrade
						// gradeDate
						
						final String camis = restaurant.getString("camis");
						final String restaurantName = restaurant.getString("doingBusinessAs");
					
						final String sBorough = restaurant.getString("borough");
					
						// NOT findByCode
						final Borough borough = Borough.findByName(sBorough);
						
						// The address is an embedded JSON object
						final String address = restaurant.getString("address");
						
						final JSONTokener addrTokener = new JSONTokener(address);
						final JSONObject oAddress = (JSONObject) addrTokener.nextValue();
						
						final String building = oAddress.getString("building");
						final String street = oAddress.getString("street");
						final String zipCode = oAddress.getString("zipCode");
						
						// back to our restaurants
						final String phone = restaurant.getString("phone");
						final String cuisineCode = restaurant.getString("cuisineCode");
						final String gradeName = restaurant.getString("currentGrade");
						
						// NOT findByCode
						final Grade currentGrade = Grade.findByName(gradeName);
						
						final Date gradeDate;
						
						final String dateString = restaurant.getString("gradeDate");
						
						if (dateString == null || dateString == "") {
							gradeDate = null;
						} else {
							
							// Sample date format:
							// Aug 30, 2012 9:12:15 AM
							gradeDate = simpleDateFormat.parse(dateString);
							
						}
						
						aRestaurants.add(new Restaurant(camis, restaurantName, borough, new Address(building, street, zipCode), phone, cuisineCode, currentGrade, gradeDate ));
												
						result.add(restaurantName);
					}
				} finally {
					
					urlConnection.disconnect();
					
					if (aRestaurants.size() > 0) {
						
					    restaurantDatabase.saveRestaurants(aRestaurants);
					    status = aRestaurants.size() + " restaurants retrieved.";
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				status = "Error retrieving restaurants: " + e.getMessage();
				
			}

			return status;
		}
		
		protected void onPostExecute(String result) {
			
			dialog.hide();
			
			Toast.makeText(NewEatsNewYorkActivity.this, result, Toast.LENGTH_LONG).show();
								
		}
	}
	
	private class GetLastUpdateTask extends AsyncTask<String, Integer, String> {
		
		protected String doInBackground(String...strings) {
			
			Date date = new RestaurantDatabase(NewEatsNewYorkActivity.this).getLastUpdateDate();
		
			if (date == null) {
				return "No prior update date";
			} else {
				return date.toString();
			}
			
		}
		
		protected void onPostExecute(String status) {
			
			TextView tvLastUpdateDate = (TextView) findViewById(R.id.tvLastUpdateDate);			
			tvLastUpdateDate.setText("Last update date: " + status);
			
		}
	}
	
}