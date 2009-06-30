package net.nycjava.skylight.service.impl;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.SensorAppliedForceAdapter;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class SensorAppliedForceAdapterServiceAndroidImpl implements SensorAppliedForceAdapter {

	@Dependency
	BalancedObjectPublicationService balancedPublicationService;

	@Dependency
	private SensorManager mSensorManager;

	private long lastTime;

	private final SensorListener mListener = new SensorListener() {
		public void onSensorChanged(int sensor, float[] values) {
			long thisTime = System.currentTimeMillis();
			// calc angle & force ...
			// Not sure if I want to this here as it will be calculated quite frequently
			float x = (float) values[0] * ((float) thisTime - (float) lastTime) / 1000f;
			float y = (float) values[1] * ((float) thisTime - (float) lastTime) / 1000f;
			balancedPublicationService.applyForce(x, y);
			lastTime = thisTime;
		}

		public void onAccuracyChanged(int sensor, int accuracy) {
			// ???
		}
	};

	public void start() {
		int mask = 0;
		mask |= SensorManager.SENSOR_ACCELEROMETER;
		lastTime = System.currentTimeMillis();
		mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void stop() {
		mSensorManager.unregisterListener(mListener);
	}

}
