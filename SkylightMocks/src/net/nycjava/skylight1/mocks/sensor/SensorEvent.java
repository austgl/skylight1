package net.nycjava.skylight1.mocks.sensor;

import static java.lang.String.format;

public abstract class SensorEvent {
	private final long time;

	private final int sensorId;

	public SensorEvent(long aTime, int aSensorId) {
		time = aTime;
		sensorId = aSensorId;
	}

	public long getTime() {
		return time;
	}

	public int getSensorId() {
		return sensorId;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SensorEvent)) {
			return false;
		}
		final SensorEvent sensorEvent = ((SensorEvent) o);
		return sensorEvent.time == time && sensorEvent.sensorId == sensorId;
	}

	@Override
	public String toString() {
		return format("%s: %d, %d", this.getClass().getName(), time, sensorId);
	}
}