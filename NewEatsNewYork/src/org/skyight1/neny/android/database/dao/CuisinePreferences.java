package org.skyight1.neny.android.database.dao;

import android.content.Context;

public class CuisinePreferences extends AbstractPreferences implements
		PreferencesDao {

	public CuisinePreferences(Context aContext){
		preferences = aContext.getSharedPreferences("cuisines", Context.MODE_PRIVATE);
	}

}
