package net.nycjava.skylight.mocks.camera;

import static java.lang.String.format;

public abstract class CameraEvent {
	private final long time;

	public CameraEvent(long aTime) {
		time = aTime;
	}

	public long getTime() {
		return time;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CameraEvent)) {
			return false;
		}
		final CameraEvent sensorEvent = ((CameraEvent) o);
		return sensorEvent.time == time;
	}

	@Override
	public String toString() {
		return format("%s: %d", this.getClass().getName(), time);
	}
}