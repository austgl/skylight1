package org.skylight1.neny.android.database.model;

import org.skylight1.neny.android.R;

public enum Cuisine {
	
	AFRICAN("African", R.drawable.africa_active),
	ASIAN("Asian", R.drawable.china_active),
	CENTRAL_AND_SOUTH_AMERICAN("South American", R.drawable.mayan_active),
	COMFORT_AND_SNACKS("Comfort", R.drawable.comfort_active),
	EUROPEAN("European", R.drawable.eu_active),
	ITALIAN("Italian", R.drawable.italian_active),
	MIDDLE_EASTERN("Middle Eastern", R.drawable.middle_eastern_active),
	US_NORTH_AMERICAN("North American", R.drawable.north_america_active),
	INDIAN_SUBCONTINENT("Indian", R.drawable.indian_active),
	ECLECTIC("Eclectic", R.drawable.eclectic_active),
	PACIFICA("Pacifica", R.drawable.pacifica_active),
	VEGETARIAN("Vegetarian", R.drawable.vege_active);

	private final String label;
	private final int activeImageResourceId;

	private Cuisine(String aLabel, int anActiveImageResourceId) {
		label = aLabel;
		activeImageResourceId = anActiveImageResourceId;
	}

	public String getLabel() {
		return label;
	}

	public int getActiveImageResourceId() {
		return activeImageResourceId;
	}
	
	public static Cuisine findCuisineByMajorCuisineLabel(final String aMajorCategory) {
		for (final Cuisine cuisine : values()) {
			if (cuisine.getLabel().equals(aMajorCategory)) {
				return cuisine;
			}
		}
		
		throw new RuntimeException(aMajorCategory + " is not a major category (label)");
	}
}
