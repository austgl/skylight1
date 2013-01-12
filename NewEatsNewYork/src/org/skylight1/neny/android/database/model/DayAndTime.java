package org.skylight1.neny.android.database.model;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;
import static org.skylight1.neny.android.database.model.MealTime.DINNER;
import static org.skylight1.neny.android.database.model.MealTime.LUNCH;

import java.util.Calendar;

public enum DayAndTime {
	
	SUNDAY_LUNCH(SUNDAY, LUNCH),
	SUNDAY_DINNER(SUNDAY, DINNER),
	MONDAY_LUNCH(MONDAY, LUNCH),
	MONDAY_DINNER(MONDAY, DINNER),
	TUESDAY_LUNCH(TUESDAY, LUNCH),
	TUESDAY_DINNER(TUESDAY, DINNER),
	WEDNESDAY_LUNCH(WEDNESDAY, LUNCH),
	WEDNESDAY_DINNER(WEDNESDAY, DINNER),
	THURSDAY_LUNCH(THURSDAY, LUNCH),
	THURSDAY_DINNER(THURSDAY, DINNER),
	FRIDAY_LUNCH(FRIDAY, LUNCH),
	FRIDAY_DINNER(FRIDAY, DINNER),
	SATURDAY_LUNCH(SATURDAY, LUNCH),
	SATURDAY_DINNER(SATURDAY, DINNER);
	
	private int dayOfWeek;
	private MealTime mealTime;
	
	private DayAndTime(final int aDayOfWeek, final MealTime aMealTime){
		dayOfWeek = aDayOfWeek;
		mealTime = aMealTime;
	}
	
	public static DayAndTime findByDayOfWeekAndMealTime(final int aDayOfWeek, final MealTime aMealTime){
		for (final DayAndTime dayAndtime : values()) {
			if (dayAndtime.dayOfWeek == aDayOfWeek &  dayAndtime.mealTime == aMealTime) {
				return dayAndtime;
			}
		}
		return null;
	}
}
