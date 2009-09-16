package net.nycjava.skylight1.mocks.camera;

import static java.lang.String.format;

import java.util.Arrays;

public class CameraPreviewEvent extends CameraEvent {
	private final byte[] data;

	public CameraPreviewEvent(final long aTime, final byte[] aData) {
		super(aTime);
		data = aData;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CameraPreviewEvent)) {
			return false;
		}
		return super.equals(o) && Arrays.equals(((CameraPreviewEvent) o).data, data);
	}

	@Override
	public String toString() {
		return super.toString() + format(", %d", Arrays.toString(data));
	}
}
