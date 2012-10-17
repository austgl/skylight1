package org.skyight1.neny.android.database.dao;

import org.skylight1.neny.android.database.model.Neighborhood;

//import org.skylight1.neny.android.database
public interface PreferencesDao {

	boolean setPreferences(String value,boolean isSelected);
	boolean getPreference(String value);
	
}
