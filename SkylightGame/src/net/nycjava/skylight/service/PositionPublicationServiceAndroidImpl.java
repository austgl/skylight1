package net.nycjava.skylight.service;

import java.util.HashSet;
import java.util.Set;

import net.nycjava.skylight.dependencyinjection.Dependency;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class PositionPublicationServiceAndroidImpl implements PositionPublicationService {

	private Set<PositionObserver> positionObservers = new HashSet<PositionObserver>();

	@Dependency
	private SensorManager mSensorManager;

	// mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

	private final SensorListener mListener = new SensorListener() {

		long old_tick;

		long tick;

		long delta_t;

		float x;

		float y;

		float z;

		float x_pos; // x-position in meters

		float y_pos; // y-position in meters

		float z_pos; // z-position in meters

		double y_dist = 0.0f; // y-distance traveled in meters

		double y_vel = 0.0f; // y-velocity in meters/sec

		double y_accel = 0.0f; // y-acceleration in meter/sec^2

		double y_fudge = 0.1;

		public void onSensorChanged(int sensor, float[] values) {
			tick = System.currentTimeMillis();
			// if(SensorManager.SENSOR_ACCELEROMETER==sensor) //todo: is this check needed?
			// do math for creating position
			x = values[SensorManager.DATA_X];
			y = values[SensorManager.DATA_Y];
			z = values[SensorManager.DATA_Z];

			if (Math.abs(y) >= y_fudge) {
				delta_t = tick - old_tick;
				y_dist += y_vel * ((delta_t) / 1000.0);
				y_vel += y_accel * ((delta_t) / 1000.0);
				y_accel = y;
			}
			old_tick = tick;
			notifyObservers(new Position(0.0f, (float) y_dist, 0.0f));

		}

		public void onAccuracyChanged(int sensor, int accuracy) {
			// todo: ???
		}
	};

	private void open() {
		int mask = 0;
		mask |= SensorManager.SENSOR_ACCELEROMETER;
		mSensorManager.registerListener(mListener, mask, SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void close() {
		mSensorManager.unregisterListener(mListener);
	}

	public void addObserver(PositionObserver anObserver) {
		positionObservers.add(anObserver);
		if (positionObservers.size() == 1)
			open();
	}

	public boolean removeObserver(PositionObserver anObserver) {
		final boolean existed = positionObservers.remove(anObserver);
		if (positionObservers.isEmpty())
			close();
		return existed;
	}

	private void notifyObservers(Position aPosition) {
		for (PositionObserver positionObserver : positionObservers) {
			positionObserver.positionNotification(aPosition);
		}
	}

}
