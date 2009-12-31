package skylight1.toast;

import static skylight1.toast.ToastActivity.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Detect tilt start and end.
 *
 */
public class TiltDetector implements SensorEventListener {
	
	private static final String LOG_TAG = TiltDetector.class.getSimpleName();
	
	private static final int TRIGGER_DIFFERENCE = 30;

	//Orientation values for when device is pointing up.
	private static final int POINTING_UP_PITCH = -90;
	private static final int POINTING_UP_ROLL = 0;
	
	private ToastActivity mToast;
	
	private boolean mPreviousTilted;
	
	public TiltDetector(final ToastActivity toast) {
		super();
		this.mToast = toast;
	}	
	
	private boolean isAxisTilted(final int pointingUp, final float currentValue) {
		final int lowThreshold = pointingUp - TRIGGER_DIFFERENCE;
		final int highThreshold = pointingUp + TRIGGER_DIFFERENCE;
	
		if ( currentValue > lowThreshold 
				&& currentValue < highThreshold ) {

			if ( LOG ) Log.i(LOG_TAG, "Axis not tilted. Current value, " + currentValue + ", is close to pointing up value, " + pointingUp);
			return false;

		} else {
			if ( LOG ) Log.i(LOG_TAG, "Axis tilted. Current value, " + currentValue + ", is far enough from pointing up value, " + pointingUp);
			return true;
		}
	}
	
	public void onSensorChanged(SensorEvent event) {
		
		if ( LOG ) Log.i(LOG_TAG, "onSensorChanged : Azimuth = " + event.values[0]
		+ ", Pitch = " + event.values[1] + ", Roll = " + event.values[2]);

		//event.values[0] is not used, it's the direction the phone is pointing along the ground.
		boolean isTilted = isAxisTilted(POINTING_UP_PITCH, event.values[1]);
		isTilted |= isAxisTilted(POINTING_UP_ROLL, event.values[2]);

		if ( mPreviousTilted && !isTilted ) {
			mPreviousTilted = false;
			mToast.onTiltEnded();
		} else if ( !mPreviousTilted && isTilted ) {
			mPreviousTilted = true;
			mToast.onTilt();
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//Do nothing.
	}

}
