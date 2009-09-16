package net.nycjava.skylight1.mocks.sensor;

import static java.lang.String.format;

public class SensorAccuracyEvent extends SensorEvent {
	private final int accuracy;

	public SensorAccuracyEvent(final long aTime, final int aSensorId, final int anAccuracy) {
		super(aTime, aSensorId);
		accuracy = anAccuracy;
	}

	public int getAccuracy() {
		return accuracy;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SensorAccuracyEvent)) {
			return false;
		}
		return super.equals(o) && ((SensorAccuracyEvent) o).accuracy == accuracy;
	}

	@Override
	public String toString() {
		return super.toString() + format(", %d", accuracy);
	}
}
