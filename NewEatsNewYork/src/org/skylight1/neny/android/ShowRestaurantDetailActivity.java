package org.skylight1.neny.android;

import org.skylight1.neny.android.database.RestaurantDatabase;
import org.skylight1.neny.android.database.model.Address;
import org.skylight1.neny.android.database.model.Restaurant;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowRestaurantDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);

		setContentView(R.layout.restaurant_detail);

		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String camis = extras.getString("camis");
			if (camis != null) {
				final Restaurant restaurant = new RestaurantDatabase(this).getRestaurantByCamis(camis);
				if (restaurant != null) {
					showRestaurantDetail(restaurant);
				}
			}
		}
	}

	private void showRestaurantDetail(Restaurant restaurant) {
		final TextView tvRestaurantName = (TextView) findViewById(R.id.tv_detail_restaurant_name);
		tvRestaurantName.setText(restaurant.getDoingBusinessAs());

		final TextView tvRestaurantPhone = (TextView) findViewById(R.id.tv_detail_restaurant_phone);
		tvRestaurantPhone.setText(restaurant.getPhone());

		final Button callButton = (Button) findViewById(R.id.bn_call);
		callButton.setTag(restaurant.getPhone());

		final Address address = restaurant.getAddress();
		final TextView tvStreet = (TextView) findViewById(R.id.tv_detail_restaurant_street);
		final TextView tvZipCode = (TextView) findViewById(R.id.tv_detail_restaurant_zipcode);
		final String street = address.getBuilding() + " " + address.getStreet();
		tvStreet.setText(street);
		tvZipCode.setText(address.getZipCode());
	}

	public void callRestaurant(View v) {
		final Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + v.getTag()));
		startActivity(callIntent);
	}
}
