package skylight1.toast;

import static skylight1.toast.ToastActivity.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Detect tilts.
 *
 */
public class TiltDetector implements SensorEventListener {
	
	private static final String LOG_TAG = "TiltDetector";
	
	private static final int TRIGGER_DIFFERENCE = 20;

	//Orientation values for when device is pointing up.
	private static final int[] POINTING_UP = {90, -90, 0};
	
	private ToastActivity mToast;
	
	public TiltDetector(ToastActivity toast) {
		super();
		this.mToast = toast;
	}	
	
	private void checkAxis(int pointingUp, float currentValue) {
		if ( LOG ) Log.i(LOG_TAG, "currentValue: " + currentValue);
		
		if ( LOG ) Log.i(LOG_TAG, "Poiting up is: " + pointingUp);
		final int lowThreshold = pointingUp - TRIGGER_DIFFERENCE;
		final int highThreshold = pointingUp + TRIGGER_DIFFERENCE;

		if ( LOG ) Log.i(LOG_TAG, "lowThreshold: " + lowThreshold);
		if ( LOG ) Log.i(LOG_TAG, "highThreshold: " + highThreshold);
		
		if ( ( currentValue < lowThreshold )
			|| currentValue > highThreshold ) {
			
			if ( LOG ) Log.i(LOG_TAG, "triggered");
			mToast.onTilt();
		}		
	}
	
	public void onSensorChanged(SensorEvent event) {
		
		if ( LOG ) Log.i(LOG_TAG, "Azimuth = " + event.values[0]
		+ ", Pitch = " + event.values[1] + ", Roll = " + event.values[2]);

		//checkAxis(POINTING_UP[0], event.values[0]);
		checkAxis(POINTING_UP[1], event.values[1]);
		checkAxis(POINTING_UP[2], event.values[2]);

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
