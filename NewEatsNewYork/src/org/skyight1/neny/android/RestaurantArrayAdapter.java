package org.skyight1.neny.android;

import java.util.ArrayList;

import org.skyight1.neny.android.database.model.Restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RestaurantArrayAdapter extends ArrayAdapter<Restaurant> {

	private Context mContext;
	private ArrayList<Restaurant> restaurants;
	
	public RestaurantArrayAdapter(Context context, ArrayList<Restaurant> restaurants) {
		
		super(context, R.layout.restaurant_row, restaurants);
		
		this.mContext = context;
		this.restaurants = restaurants;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = inflater.inflate(R.layout.restaurant_row, parent, false);
			
		}
	
		TextView tvName = (TextView) v.findViewById(R.id.tv_restaurant_name);
		TextView tvPhone = (TextView) v.findViewById(R.id.tv_restaurant_phone);
		TextView tvAddress = (TextView) v.findViewById(R.id.tv_restaurant_address);
		TextView tvGrade = (TextView) v.findViewById(R.id.tv_restaurant_grade);
		
		Restaurant restaurant = restaurants.get(position);
		
		tvName.setText(restaurant.getDoingBusinessAs());
		tvPhone.setText(restaurant.getPhone());
		tvAddress.setText(restaurant.getAddress().getStreet());
		
		// a little kludgy
		tvGrade.setText(restaurant.getCurrentGrade().toString());
		
		return v;
		
	}
	
}
