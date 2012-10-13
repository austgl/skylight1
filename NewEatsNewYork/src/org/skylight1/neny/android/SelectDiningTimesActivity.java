package org.skylight1.neny.android;

import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SelectDiningTimesActivity extends Activity{

	private static final String TAG ="SelectDiningTimesActivity";
	private SharedPreferences preferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		preferences = getSharedPreferences("dining_times",MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_view);
		Log.i(TAG, "Select Dining Activities");
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox11),String.valueOf(Calendar.SUNDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox12),String.valueOf(Calendar.SUNDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox21),String.valueOf(Calendar.MONDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox22),String.valueOf(Calendar.MONDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox31),String.valueOf(Calendar.TUESDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox32),String.valueOf(Calendar.TUESDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox41),String.valueOf(Calendar.WEDNESDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox42),String.valueOf(Calendar.WEDNESDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox51),String.valueOf(Calendar.THURSDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox52),String.valueOf(Calendar.THURSDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox61),String.valueOf(Calendar.FRIDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox62),String.valueOf(Calendar.FRIDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));
		
		setCheckBox((CheckBox)findViewById(R.id.checkBox71),String.valueOf(Calendar.SATURDAY)+String.valueOf(NewEatsNewYorkApplication.LUNCH));
		setCheckBox((CheckBox)findViewById(R.id.checkBox72),String.valueOf(Calendar.SATURDAY)+String.valueOf(NewEatsNewYorkApplication.DINNER));

	}
	
	private void setCheckBox(CheckBox checkbox,final String mealDayAndTime){
		checkbox.setChecked(preferences.getBoolean(mealDayAndTime, false));
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				final Editor edit = preferences.edit();
				edit.putBoolean(mealDayAndTime, isChecked);
			
				edit.commit();
				
			}
			
		});
	}

}
