package net.nycjava.skylight.service;

public interface CameraObscurementObserver {
	public static enum CameraObscurementState {
		obscured, unobscured;
	}

	/**
	 * Called intermittently to notify the observer of the obscuring and unobscuring of the device's camera.
	 */
	void cameraObscurementNotification(CameraObscurementState aCameraObscuredState);
}
