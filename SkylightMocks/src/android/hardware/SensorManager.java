package android.hardware;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.nycjava.skylight.mocks.file.SensorEventStreamReader;
import net.nycjava.skylight.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight.mocks.sensor.SensorEvent;
import net.nycjava.skylight.mocks.sensor.SensorValuesEvent;
import android.util.Log;

public class SensorManager {

	public static final int SENSOR_ORIENTATION = 1;

	public static final int SENSOR_ACCELEROMETER = 2;

	public static final int SENSOR_TEMPERATURE = 4;

	public static final int SENSOR_MAGNETIC_FIELD = 8;

	public static final int SENSOR_LIGHT = 16;

	public static final int SENSOR_PROXIMITY = 32;

	public static final int SENSOR_TRICORDER = 64;

	public static final int SENSOR_ORIENTATION_RAW = 128;

	public static final int SENSOR_ALL = 127;

	public static final int SENSOR_MIN = 1;

	public static final int SENSOR_MAX = 64;

	public static final int DATA_X = 0;

	public static final int DATA_Y = 1;

	public static final int DATA_Z = 2;

	public static final int RAW_DATA_INDEX = 3;

	public static final int RAW_DATA_X = 3;

	public static final int RAW_DATA_Y = 4;

	public static final int RAW_DATA_Z = 5;

	public static final float STANDARD_GRAVITY = 9.80665f;

	public static final float GRAVITY_SUN = 275.0f;

	public static final float GRAVITY_MERCURY = 3.7f;

	public static final float GRAVITY_VENUS = 8.87f;

	public static final float GRAVITY_EARTH = 9.80665f;

	public static final float GRAVITY_MOON = 1.6f;

	public static final float GRAVITY_MARS = 3.71f;

	public static final float GRAVITY_JUPITER = 23.12f;

	public static final float GRAVITY_SATURN = 8.96f;

	public static final float GRAVITY_URANUS = 8.69f;

	public static final float GRAVITY_NEPTUNE = 11.0f;

	public static final float GRAVITY_PLUTO = 0.6f;

	public static final float GRAVITY_DEATH_STAR_I = 3.5303614E-7f;

	public static final float GRAVITY_THE_ISLAND = 4.815162f;

	public static final float MAGNETIC_FIELD_EARTH_MAX = 60.0f;

	public static final float MAGNETIC_FIELD_EARTH_MIN = 30.0f;

	public static final float LIGHT_SUNLIGHT_MAX = 120000.0f;

	public static final float LIGHT_SUNLIGHT = 110000.0f;

	public static final float LIGHT_SHADE = 20000.0f;

	public static final float LIGHT_OVERCAST = 10000.0f;

	public static final float LIGHT_SUNRISE = 400.0f;

	public static final float LIGHT_CLOUDY = 100.0f;

	public static final float LIGHT_FULLMOON = 0.25f;

	public static final float LIGHT_NO_MOON = 0.0010f;

	public static final int SENSOR_DELAY_FASTEST = 0;

	public static final int SENSOR_DELAY_GAME = 1;

	public static final int SENSOR_DELAY_UI = 2;

	public static final int SENSOR_DELAY_NORMAL = 3;

	public static final int SENSOR_STATUS_UNRELIABLE = 0;

	public static final int SENSOR_STATUS_ACCURACY_LOW = 1;

	public static final int SENSOR_STATUS_ACCURACY_MEDIUM = 2;

	public static final int SENSOR_STATUS_ACCURACY_HIGH = 3;

	private int sensors = SENSOR_ALL;

	private final List<SensorListener> listeners = new ArrayList<SensorListener>();

	private final InputStream inputStream;

	private boolean started;

	SensorManager(InputStream anInputStream) {
		inputStream = anInputStream;
	}

	void setSensors(int aSensors) {
		sensors = aSensors;
	}

	private void startIfNotAlreadyStarted() {
		if (!started) {

			new Thread(new Runnable() {
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
	}

	public int getSensors() {
		return sensors;
	}

	public boolean registerListener(SensorListener listener, int sensors, int rate) {
		listeners.add(listener);
		startIfNotAlreadyStarted();
		return true;
	}

	public boolean registerListener(SensorListener listener, int sensors) {
		listeners.add(listener);
		startIfNotAlreadyStarted();
		return true;
	}

	public void unregisterListener(SensorListener listener, int sensors) {
		listeners.remove(listener);
	}

	public void unregisterListener(SensorListener listener) {
		listeners.remove(listener);
	}
}
