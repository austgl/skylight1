package org.skyight1.neny.android.database.dao;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class AbstractPreferences implements PreferencesDao {

	@Override
	public boolean setPreferences(String value, boolean isSelected) {
		final Editor edit = getEditor();
		
		edit.putBoolean(value, isSelected);
		return edit.commit();
	}

	public abstract Editor getEditor();
	
	protected SharedPreferences preferences;

}
