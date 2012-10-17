package org.skyight1.neny.android.database.dao;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class CuisinePreferences extends AbstractPreferences implements
		PreferencesDao {

	public CuisinePreferences(Context aContext){
		preferences = aContext.getSharedPreferences("cuisines", Context.MODE_PRIVATE);
	}
	@Override
	public boolean getPreference(String key, boolean defaultValue) {
		return preferences.getBoolean(key, defaultValue);
	}

	@Override
	public Editor getEditor() {
		return preferences.edit();
	}

}
