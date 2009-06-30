package net.nycjava.skylight.service.impl;

import net.nycjava.skylight.dependencyinjection.Dependency;
import net.nycjava.skylight.service.BalancedObjectPublicationService;
import net.nycjava.skylight.service.SensorAppliedForceAdapter;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class SensorAppliedForceAdapterServiceAndroidImpl implements SensorAppliedForceAdapter {

	private static final int Y_AXIS = 1;

	private static final int X_AXIS = 0;

	private static final float MILLISECONDS_IN_A_SECOND = 1000f;

	private static final float FORCE_FACTOR = 0.2f;

	@Dependency
	BalancedObjectPublicationService balancedPublicationService;

	@Dependency
	private SensorManager mSensorManager;

	private long lastTime;

	private final SensorListener mListener = new SensorListener() {
		public void onSensorChanged(int sensor, float[] values) {
			final long thisTime = System.currentTimeMillis();
			final float scaledForceFactor = (float) (thisTime - lastTime) / MILLISECONDS_IN_A_SECOND * FORCE_FACTOR;
			final float x = values[X_AXIS] * scaledForceFactor;
			final float y = values[Y_AXIS] * scaledForceFactor;
			balancedPublicationService.applyForce(x, -y);
			lastTime = thisTime;
		}

		public void onAccuracyChanged(int sensor, int accuracy) {
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
