package net.nycjava.skylight1.mocks.file;

import static java.lang.String.format;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.nycjava.skylight1.mocks.sensor.SensorAccuracyEvent;
import net.nycjava.skylight1.mocks.sensor.SensorEvent;
import net.nycjava.skylight1.mocks.sensor.SensorValuesEvent;

// TODO change to use java.nio
// TODO make more OO - use polymorphism
public class SensorEventStreamWriter {
	static final byte SENSOR_VALUES_EVENT = 0;

	static final byte SENSOR_ACCURACY_EVENT = 1;

	private final DataOutputStream dataOutputStream;

	public SensorEventStreamWriter(OutputStream anOutputStream) {
		dataOutputStream = new DataOutputStream(anOutputStream);
	}

	public void writeSensorEvent(SensorEvent aSensorEvent) {
		try {
			final long time = aSensorEvent.getTime();
			dataOutputStream.writeLong(time);
			dataOutputStream.writeInt(aSensorEvent.getSensorId());
			if (aSensorEvent instanceof SensorValuesEvent) {
				dataOutputStream.writeByte(SENSOR_VALUES_EVENT);
				final SensorValuesEvent sensorValuesEvent = (SensorValuesEvent) aSensorEvent;
				final float[] values = sensorValuesEvent.getValues();
				dataOutputStream.writeInt(values.length);
				for (final float value : values) {
					dataOutputStream.writeFloat(value);
				}
			} else {
				dataOutputStream.writeByte(SENSOR_ACCURACY_EVENT);
				SensorAccuracyEvent sensorAccuracyEvent = (SensorAccuracyEvent) aSensorEvent;
				dataOutputStream.writeInt(sensorAccuracyEvent.getAccuracy());
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(format("Unable to write to %s.", dataOutputStream), e);
		}
	}
}
