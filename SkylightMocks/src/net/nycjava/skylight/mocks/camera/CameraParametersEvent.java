package net.nycjava.skylight.mocks.camera;

import static java.lang.String.format;
import android.hardware.Camera;

public class CameraParametersEvent extends CameraEvent {
	private final Camera.Parameters parameters;

	public CameraParametersEvent(long aTime, Camera.Parameters aParameters) {
		super(aTime);
		parameters = aParameters;
	}

	public Camera.Parameters getParameters() {
		return parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CameraParametersEvent)) {
			return false;
		}
		final CameraParametersEvent cameraParametersEvent = ((CameraParametersEvent) o);
		return super.equals(o) && parameters.equals(cameraParametersEvent);
	}

	@Override
	public String toString() {
		return super.toString() + format(", %s", parameters.flatten());
	}
}
