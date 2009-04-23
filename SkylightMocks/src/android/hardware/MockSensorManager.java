package android.hardware;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.nycjava.skylight.mocks.file.SensorEventStreamReader;
import net.nycjava.skylight.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight.mocks.sensor.SensorEvent;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;
import android.util.Log;

public class MockSensorManager extends SensorManager {
	private int sensors = SENSOR_ALL;

	private final List<SensorListener> listeners = new ArrayList<SensorListener>();

	private final InputStream inputStream;

	private boolean started;

	public MockSensorManager(InputStream anInputStream) {
		inputStream = anInputStream;
	}

	public void setSensors(int aSensors) {
		sensors = aSensors;
	}

	private void startIfNotAlreadyStarted() {
		if (started) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				SensorEventStreamReader sensorEventStreamReader = new SensorEventStreamReader(inputStream);
				SensorEvent sensorEvent;
				while ((sensorEvent = sensorEventStreamReader.readSensorEvent()) != null) {
					long delay = sensorEvent.getTime() + startTime - System.currentTimeMillis();
					if (delay > 0L) {
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							Log.e("mocks", null, e);
						}
					}

					if (sensorEvent instanceof SensorAccuracyEvent) {
						for (SensorListener sensorListener : listeners) {
							SensorAccuracyEvent sensorAccuracyEvent = (SensorAccuracyEvent) sensorEvent;
							sensorListener.onAccuracyChanged(sensorAccuracyEvent.getSensorId(), sensorAccuracyEvent
									.getAccuracy());
						}
					} else {
						for (SensorListener sensorListener : listeners) {
							SensorValuesEvent sensorValuesEvent = (SensorValuesEvent) sensorEvent;
							sensorListener.onSensorChanged(sensorValuesEvent.getSensorId(), sensorValuesEvent
									.getValues());
						}
					}
				}
			}
		}).start();
		started = true;
	}

	@Override
	public int getSensors() {
		return sensors;
	}

	@Override
	public boolean registerListener(SensorListener listener, int sensors, int rate) {
		listeners.add(listener);
		startIfNotAlreadyStarted();
		return true;
	}

	@Override
	public boolean registerListener(SensorListener listener, int sensors) {
		listeners.add(listener);
		startIfNotAlreadyStarted();
		return true;
	}

	@Override
	public void unregisterListener(SensorListener listener, int sensors) {
		listeners.remove(listener);
	}

	@Override
	public void unregisterListener(SensorListener listener) {
		listeners.remove(listener);
	}
}
