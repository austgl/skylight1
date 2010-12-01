package net.nycjava.skylight1.service.impl;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.nycjava.skylight1.dependencyinjection.Dependency;
import net.nycjava.skylight1.service.CompassService;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;

public class CompassServiceAndroidImp implements CompassService {

	@Dependency
	SensorManager sensorManager;

	@Override
	public Future<Float> getCompassReading() {
		Log.i(CompassServiceAndroidImp.class.getName(), "getCompassReading");
		final Future<Float> result = Executors.newSingleThreadExecutor().submit(new Callable<Float>() {
			final private BlockingQueue<Float> listenerResult = new ArrayBlockingQueue<Float>(1);

			@Override
			public Float call() throws Exception {

				Log.i(CompassServiceAndroidImp.class.getName(), "registering listener");
				sensorManager.registerListener(new SensorListener() {
					@Override
					public void onAccuracyChanged(int sensor, int accuracy) {
					}

					@Override
					public void onSensorChanged(int sensor, float[] values) {
						sensorManager.unregisterListener(this, sensor);
						Log.i(CompassServiceAndroidImp.class.getName(), "just received temp sensor event "
								+ Arrays.toString(values) + " for sensor " + sensor);
						listenerResult.offer(values[0]);
					}
				}, SensorManager.SENSOR_ORIENTATION_RAW, SensorManager.SENSOR_DELAY_FASTEST);
				Log.i(CompassServiceAndroidImp.class.getName(), "now waiting for result");
				final Float resultFromQueue = listenerResult.take();
				Log.i(CompassServiceAndroidImp.class.getName(), "result from queue was " + resultFromQueue);
				return resultFromQueue;
			}
		});
		Log.i(CompassServiceAndroidImp.class.getName(), "returning result " + result);
		return result;
	}
}
