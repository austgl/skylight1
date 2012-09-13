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

	private static final int COL_CAMIS = 0;
	private static final int COL_DOING_BUSINESS_AS = 1;
	private static final int COL_BOROUGH = 2;
	private static final int COL_BUILDING = 3;
	private static final int COL_STREET = 4;
	private static final int COL_ZIP_CODE = 5;
	private static final int COL_PHONE = 6;
	private static final int COL_CUISINE_CODE = 7;
	private static final int COL_CURRENT_GRADE = 8;
	private static final int COL_GRADE_DATE = 9;

	private static final int COL_UPDATE_DATE = 0;
	
	private SQLiteOpenHelper sqLiteOpenHelper;

	public RestaurantDatabase(final Context aContext) {
				
		sqLiteOpenHelper = new SQLiteOpenHelper(aContext, "restaurants", null, CURRENT_DATABASE_VERSION) {
			@Override
			public void onCreate(SQLiteDatabase aDb) {
				aDb.execSQL("create table restaurant (camis TEXT, doingBusinessAs TEXT, borough TEXT, building TEXT, street TEXT, zipCode TEXT, phone TEXT, cuisineCode TEXT, currentGrade TEXT, gradeDate TEXT)");
				aDb.execSQL("create table last_update (update_date TEXT)");
			}

			@Override
			public void onUpgrade(SQLiteDatabase aDb, int anOldVersion, int aNewVersion) {
				throw new RuntimeException(format("No upgrade path exists for upgrading from version %d to version %d", anOldVersion, aNewVersion));
			}
		};
	}

	public List<Restaurant> getRestaurants() {
		
		// this is used to handle the parsing of the received gradeDate value
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		final List<Restaurant> result = new ArrayList<Restaurant>();
		
		final SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
		
		try {
			final Cursor cursor = database.query("restaurant", null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				
				final String camis = cursor.getString(COL_CAMIS);
				
				final String doingBusinessAs = cursor.getString(COL_DOING_BUSINESS_AS);
				
				final Borough borough = Borough.valueOf(cursor.getString(COL_BOROUGH));
				
				final String building = cursor.getString(COL_BUILDING);
				final String street = cursor.getString(COL_STREET);
				final String zipCode = cursor.getString(COL_ZIP_CODE);
				
				final Address address = new Address(building, street, zipCode);
				
				final String phone = cursor.getString(COL_PHONE);
				final String cuisineCode = cursor.getString(COL_CUISINE_CODE);
				final Grade currentGrade = Grade.valueOf(cursor.getString(COL_CURRENT_GRADE));
				
				Date gradeDate;
				final String gradeDateAsString = cursor.getString(COL_GRADE_DATE);
				try {
					gradeDate = simpleDateFormat.parse(gradeDateAsString);
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
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
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
				
				String gradeDate = simpleDateFormat.format(restaurant.getGradeDate());

				contentValues.put("gradeDate", gradeDate);
				
				final long result = database.insert("restaurant", null, contentValues);
				if (result == -1) {
					throw new RuntimeException(format("Unable to insert restaurant %s", restaurant));
				}
			}
			
			// repopulate our last_update table with the current date
			database.delete("last_update", null, null);

			final ContentValues contentValues = new ContentValues();
			contentValues.put("update_date", new Date().toString());

			database.insert("last_update", null, contentValues);			
			
		} finally {
			if (database != null) {
				database.close();
			}
		}
	}
	
	public Date getLastUpdateDate() {

		final SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

		Date lastUpdateDate = null;

		try {

			final Cursor cursor = database.query("last_update", null, null,
					null, null, null, null);

			if (cursor.moveToFirst()) {
				
				String dateString = cursor.getString(COL_UPDATE_DATE);
				
				if (dateString != null) {
					try {
						lastUpdateDate = new SimpleDateFormat("EEE MMM d hh:mm:ss ZZZ yyyy").parse(dateString);
 					} catch (ParseException e) {
						
					}
				}
				
			}
		} finally {
			if (database != null) {
				database.close();
			}
		}

		return lastUpdateDate;

	}	
	
}
