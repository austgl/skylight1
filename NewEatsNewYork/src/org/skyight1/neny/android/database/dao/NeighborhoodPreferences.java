package org.skyight1.neny.android.database.dao;



import android.content.Context;

public class NeighborhoodPreferences extends AbstractPreferences {

	
	public NeighborhoodPreferences(Context aContext){
		preferences = aContext.getSharedPreferences("neighborhoods", Context.MODE_PRIVATE);
	}
	

}
