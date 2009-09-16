package net.nycjava.skylight1.mocks.sensor;

import static java.lang.String.format;

import java.util.Arrays;

public class SensorValuesEvent extends SensorEvent {
	private final float[] values;

	public SensorValuesEvent(long aTime, int aSensorId, float[] aValues) {
		super(aTime, aSensorId);
		if (aValues == null) {
			throw new IllegalArgumentException("Null is not permitted for the values parameter.");
		}
		values = aValues;
	}

	public float[] getValues() {
		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SensorValuesEvent)) {
			return false;
		}
		final SensorValuesEvent sensorValuesEvent = ((SensorValuesEvent) o);
		return super.equals(o) && Arrays.equals(sensorValuesEvent.values, values);
	}

	@Override
	public String toString() {
		return super.toString() + format(", %s", Arrays.toString(values));
	}
}
