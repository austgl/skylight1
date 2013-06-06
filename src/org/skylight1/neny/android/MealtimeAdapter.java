package org.skylight1.neny.android;

import static org.skylight1.neny.android.database.model.MealTime.DINNER;
import static org.skylight1.neny.android.database.model.MealTime.LUNCH;

import java.util.Calendar;

import org.skylight1.neny.android.database.dao.PreferencesDao;
import org.skylight1.neny.android.database.model.DayAndTime;
import org.skylight1.neny.android.database.model.MealDayTime;
import org.skylight1.neny.android.database.model.MealTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MealtimeAdapter extends ArrayAdapter<MealDayTime> {
	private Context aContext;

	private MealDayTime[] mealSelections;

	private PreferencesDao preferences;

	public MealtimeAdapter(Context context, MealDayTime[] selections, PreferencesDao preferences) {
		super(context, R.layout.diningtime_row, selections);
		aContext = context;
		this.mealSelections = selections;
		this.preferences = preferences;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View rowView;
		if (convertView != null) {
			rowView = convertView;
		} else {
			final LayoutInflater inflater = (LayoutInflater) aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.diningtime_row, parent, false);
		}
		CheckBox lunchBox = (CheckBox) rowView.findViewById(R.id.lunchCheckBox);
		TextView mealDay = (TextView) rowView.findViewById(R.id.mealDay);
		CheckBox dinnerBox = (CheckBox) rowView.findViewById(R.id.dinnerCheckBox);

		lunchBox.setChecked(mealSelections[position].isLunch());
		mealDay.setText(mapToDay(mealSelections[position].getDayOfWeek()));
		dinnerBox.setChecked(mealSelections[position].isDinner());

		lunchBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.setPreferences(mapPositionToMealTime(position, LUNCH).name(), isChecked);
			}

		});
		dinnerBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.setPreferences(mapPositionToMealTime(position, DINNER).name(), isChecked);

			}
		});

		return rowView;
	}

	private CharSequence mapToDay(int dayOfWeek) {
		switch (dayOfWeek) {
			case 1: {
				return aContext.getString(R.string.sunday);
			}
			case 2: {
				return aContext.getString(R.string.monday);
			}
			case 3: {
				return aContext.getString(R.string.tuesday);
			}
			case 4: {
				return aContext.getString(R.string.wednesday);
			}
			case 5: {
				return aContext.getString(R.string.thursday);
			}
			case 6: {
				return aContext.getString(R.string.friday);
			}
			case 7: {
				return aContext.getString(R.string.saturday);
			}
		}
		return null;
	}

	private DayAndTime mapPositionToMealTime(final int position, final MealTime mealTime) {
		final int dayOfWeek = Calendar.SUNDAY + position;
		final DayAndTime result = DayAndTime.findByDayOfWeekAndMealTime(dayOfWeek, mealTime);

		return result;
	}
}
