package net.nycjava.skylight.service.impl;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.SensorAppliedForceAdapter;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorAppliedForceAdapterServiceAndroidImpl implements SensorAppliedForceAdapter {

	private static final int Y_AXIS = 1;

	private static final int X_AXIS = 0;
	
	private static final int Z_AXIS = 2;

	private static final float MILLISECONDS_IN_A_SECOND = 1000f;

	private static final float FORCE_FACTOR = 1.0f;
	
	private static final int MILLISEC_WAIT_FOR_CALIBRATE = 300;
	
	private static final int CALIBRATE_MAX_COUNT = 10;

	private static final String TAG = "SensorAppliedForceAdapterServiceAndroidImpl";
	
	@Dependency
	BalancedObjectPublicationService balancedPublicationService;

	@Dependency
	private SensorManager mSensorManager;

	private long lastTime;
	private long calibrateDoneTime;
	private double sumX;
	private double sumY;
	private int countXY;
	private boolean calibrateDone;
	private float baseX;
	private float baseY;
	private int calibrateCount;
	private float lowX;
	private float highX;
	private float lowY;
	private float highY;
	
	private final SensorListener mListener = new SensorListener() {
		public void onSensorChanged(int sensor, float[] values) {
			final long thisTime = System.currentTimeMillis();
			final float scaledForceFactor = (float) (thisTime - lastTime) / MILLISECONDS_IN_A_SECOND;
			float x = values[X_AXIS] ;
			float y = values[Y_AXIS] ;			
			float z = values[Z_AXIS] ;
			StringBuffer valuesOut = new StringBuffer();
			if(calibrateDone == false) {
				if(Math.abs(x) > 5.0 || Math.abs(y) > 5.0 ) {
					//User is holding the phone vertically (or at least > 30 deg) 
					//so put some 'good' defaults into range and let glass drop
					lowX = -0.005f;
					highX = 0.005f;
					lowY = -0.005f;
					highY = 0.005f;
					calibrateDone = true;					
				} else {
					// user is holding holding the phone facing the sky (more or less)
					if (calibrateCount < CALIBRATE_MAX_COUNT ) {
						//TODO: replace accl calibration with time weighted average
						// do calibration of x,y
						setXRange(x);
						setYRange(y);
						sumX += x;
						sumY += y;
						countXY++;
						calibrateCount++;
//						valuesOut.append("calibrate Entry ");
//						valuesOut.append(x);
//						valuesOut.append(" ");
//						valuesOut.append(y);
						//Log.d("SensorListener",valuesOut.toString());
					} else {
						//baseX = (float) (sumX / countXY);
						//baseY = (float) (sumY / countXY);
						calibrateDone = true;
//						valuesOut.append("calibrate done");
//						valuesOut.append(lowX);
//						valuesOut.append(" ");
//						valuesOut.append(highX);
//						valuesOut.append(" ");
//						valuesOut.append(lowY);
//						valuesOut.append(" ");
//						valuesOut.append(highY);

						//Log.d("SensorListener",valuesOut.toString());
					}
				}
			} else {
				// calibration done so use it.
				//x = x - baseX;
				//y = y - baseY;
				if(true) {
					if(x < lowX) {
						x = x - lowX;
					} else if (x > highX) {
						x = x - highX;
					} else {
						x = 0.0f;
					}
					if(y < lowY) {
						y = y - lowY;
					} else if (y > highY) {
						y = y - highY;
					} else {
						y = 0.0f;
					}
					x = x * FORCE_FACTOR;
					y = y * FORCE_FACTOR;
				}
//				valuesOut.append(x);
//				valuesOut.append(" ");
//				valuesOut.append(y);
//				valuesOut.append(" ");
//				valuesOut.append(z);
				//Log.d("SensorListener ",valuesOut.toString());
				balancedPublicationService.applyForce(x, -y,(thisTime - lastTime));
			}
			lastTime = thisTime;
		}

		public void onAccuracyChanged(int sensor, int accuracy) {
		}
	};

	public void start() {
		int mask = 0;
		mask |= SensorManager.SENSOR_ACCELEROMETER;
		lastTime = System.currentTimeMillis();
		calibrateDoneTime = lastTime + MILLISEC_WAIT_FOR_CALIBRATE;
		sumX = 0;
		sumY = 0;
		countXY = 0;
		lowX = 999;
		highX = -999;
		lowY = 999;
		highY = -999;
		calibrateCount = 0;
		calibrateDone = false;
		mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_GAME);
		Log.d(TAG,"start");
	}

	public void stop() {
		mSensorManager.unregisterListener(mListener);
		Log.d(TAG,"stop");
	}
	
	private void setXRange(float x) {
		if((x < lowX) || (lowX == 999)) {
			lowX = x;
		}
		if((x > highX) || (highX == -999)) {
			highX = x;			
		}
	}

	private void setYRange(float y) {
		if((y < lowY) || (lowY == 999)) {
			lowY = y;
		}
		if((y > highY) || (highY == -999)) {
			highY = y;			
		}
	}

}
