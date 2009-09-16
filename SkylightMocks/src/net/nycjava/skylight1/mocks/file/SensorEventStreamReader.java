package net.nycjava.skylight1.mocks.file;

import static java.lang.String.format;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.nycjava.skylight1.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight1.mocks.sensor.SensorEvent;
import net.nycjava.skylight1.mocks.sensor.SensorValuesEvent;

// TODO change to use java.nio
public class SensorEventStreamReader {
	private DataInputStream dataInputStream;

	public SensorEventStreamReader(InputStream anInputStream) {
		dataInputStream = new DataInputStream(anInputStream);
	}

	public SensorEvent readSensorEvent() {
		try {
			if (dataInputStream.available() == 0) {
				return null;
			}

			final long time = dataInputStream.readLong();
			final int sensorId = dataInputStream.readInt();
			final byte type = dataInputStream.readByte();
			if (type == SensorEventStreamWriter.SENSOR_VALUES_EVENT) {
				final float[] values = new float[dataInputStream.readInt()];
				for (int i = 0; i < values.length; i++) {
					final float value = dataInputStream.readFloat();
					values[i] = value;
				}
				return new SensorValuesEvent(time, sensorId, values);
			} else {
				final int accuracy = dataInputStream.readInt();
				return new SensorAccuracyEvent(time, sensorId, accuracy);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(
					format("Unable to write to input stream %s.", dataInputStream.toString()), e);
		}
	}
}
