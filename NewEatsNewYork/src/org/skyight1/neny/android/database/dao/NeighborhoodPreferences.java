package org.skyight1.neny.android.database.dao;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NeighborhoodPreferences extends AbstractPreferences {

	private SharedPreferences preferences;
	public NeighborhoodPreferences(Context aContext){
		preferences = aContext.getSharedPreferences("neighborhoods", Context.MODE_PRIVATE);
	}
	@Override
	public Editor getEditor() {
		return preferences.edit();
	}
	@Override
	public boolean getPreference(String value, boolean defaultValue) {
		return preferences.getBoolean(value, defaultValue);
	}
	


}
