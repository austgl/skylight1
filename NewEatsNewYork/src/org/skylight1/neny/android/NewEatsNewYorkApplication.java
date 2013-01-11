package org.skylight1.neny.android;

import android.app.Application;

public class NewEatsNewYorkApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		new AlarmUtils().setAlarms(this);		
	}		
}
