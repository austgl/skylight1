package android.hardware;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.nycjava.skylight.mocks.file.SensorEventStreamReader;
import net.nycjava.skylight.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight.mocks.sensor.SensorEvent;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;

public class MockSensorManager extends SensorManager {
	private int sensors = SENSOR_ALL;

	private final List<SensorListener> listeners = new ArrayList<SensorListener>();

	private final InputStream inputStream;

	public MockSensorManager(InputStream anInputStream) {
		inputStream = anInputStream;
	}

	public void setSensors(int aSensors) {
		sensors = aSensors;
	}

	public void start() {
		SensorEventStreamReader sensorEventStreamReader = new SensorEventStreamReader(inputStream);
		SensorEvent sensorEvent;
		while ((sensorEvent = sensorEventStreamReader.readSensorEvent()) != null) {
			// TODO add delays
			if (sensorEvent instanceof SensorAccuracyEvent) {
				for (SensorListener sensorListener : listeners) {
					SensorAccuracyEvent sensorAccuracyEvent = (SensorAccuracyEvent) sensorEvent;
					sensorListener.onAccuracyChanged(sensorAccuracyEvent.getSensorId(), sensorAccuracyEvent
							.getAccuracy());
				}
			} else {
				for (SensorListener sensorListener : listeners) {
					SensorValuesEvent sensorValuesEvent = (SensorValuesEvent) sensorEvent;
					sensorListener.onSensorChanged(sensorValuesEvent.getSensorId(), sensorValuesEvent.getValues());
				}
			}
		}
	}

	@Override
	public int getSensors() {
		return sensors;
	}

	@Override
	public boolean registerListener(SensorListener listener, int sensors, int rate) {
		listeners.add(listener);
		return true;
	}

	@Override
	public boolean registerListener(SensorListener listener, int sensors) {
		listeners.add(listener);
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
