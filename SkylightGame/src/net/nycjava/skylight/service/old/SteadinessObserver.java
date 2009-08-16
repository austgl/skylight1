package net.nycjava.skylight.service.old;

public interface SteadinessObserver {
	/**
	 * Called intermittently to notify the observer of the steadiness of the device. Forces are measured in centimeters
	 * per second squared and angles in degrees.
	 */
	void steadinessNotification(float force);
	
	 // called to notify unsteadiness
	void unsteadinessNotification();
}
