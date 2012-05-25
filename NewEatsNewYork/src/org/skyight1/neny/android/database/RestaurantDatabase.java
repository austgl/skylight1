package org.skyight1.neny.android.database;

import static java.lang.String.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.skyight1.neny.android.database.model.Address;
import org.skyight1.neny.android.database.model.Borough;
import org.skyight1.neny.android.database.model.Grade;
import org.skyight1.neny.android.database.model.Restaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantDatabase {
	private static final int CURRENT_DATABASE_VERSION = 1;

	private SQLiteOpenHelper sqLiteOpenHelper;

	public RestaurantDatabase(final Context aContext) {
		sqLiteOpenHelper = new SQLiteOpenHelper(aContext, "restaurants", null, CURRENT_DATABASE_VERSION) {
			@Override
			public void onCreate(SQLiteDatabase aDb) {
				aDb.execSQL("create table restaurant (camis TEXT, doingBusinessAs TEXT, borough TEXT, building TEXT, street TEXT, zipCode TEXT, phone TEXT, cuisineCode TEXT, currentGrade TEXT, gradeDate TEXT)");
			}

			@Override
			public void onUpgrade(SQLiteDatabase aDb, int anOldVersion, int aNewVersion) {
				throw new RuntimeException(format("No upgrade path exists for upgrading from version %d to version %d", anOldVersion, aNewVersion));
			}
		};
	}

	public List<Restaurant> getRestaurants() {
		final List<Restaurant> result = new ArrayList<Restaurant>();
		final SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
		try {
			final Cursor cursor = database.query("restaurant", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				final String camis = cursor.getString(0);
				final String doingBusinessAs = cursor.getString(1);
				final Borough borough = Borough.valueOf(cursor.getString(2));
				final String building = cursor.getString(3);
				final String street = cursor.getString(4);
				final String zipCode = cursor.getString(5);
				final Address address = new Address(building, street, zipCode);
				final String phone = cursor.getString(6);
				final String cuisineCode = cursor.getString(7);
				final Grade currentGrade = Grade.valueOf(cursor.getString(8));
				Date gradeDate;
				final String gradeDateAsString = cursor.getString(9);
				try {
					gradeDate = SimpleDateFormat.getInstance().parse(gradeDateAsString);
				} catch (ParseException e) {
					throw new RuntimeException(format("Unable to parse date %s for restaurant with camis %s", gradeDateAsString, camis), e);
				}
				final Restaurant restaurant = new Restaurant(camis, doingBusinessAs, borough, address, phone, cuisineCode, currentGrade, gradeDate);
				result.add(restaurant);
			}
		} finally {
			if (database != null) {
				database.close();
			}
		}
		return result;
	}

	public void saveRestaurants(final List<Restaurant> aListOfRestaurants) {
		final SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
		try {
			// delete all of the existing rows
			database.delete("restaurant", null, null);

			// add the new ones
			for (final Restaurant restaurant : aListOfRestaurants) {
				final ContentValues contentValues = new ContentValues();
				contentValues.put("camis", restaurant.getCamis());
				contentValues.put("doingBusinessAs", restaurant.getDoingBusinessAs());
				contentValues.put("borough", restaurant.getBorough().name());
				contentValues.put("building", restaurant.getAddress().getBuilding());
				contentValues.put("street", restaurant.getAddress().getStreet());
				contentValues.put("zipCode", restaurant.getAddress().getZipCode());
				contentValues.put("phone", restaurant.getPhone());
				contentValues.put("cuisineCode", restaurant.getCuisineCode());
				contentValues.put("currentGrade", restaurant.getCurrentGrade().name());
				contentValues.put("gradeDate", restaurant.getCamis());
				final long result = database.insert("restaurant", null, contentValues);
				if (result == -1) {
					throw new RuntimeException(format("Unable to insert restaurant %s", restaurant));
				}
			}
		} finally {
			if (database != null) {
				database.close();
			}
		}
	}
}
