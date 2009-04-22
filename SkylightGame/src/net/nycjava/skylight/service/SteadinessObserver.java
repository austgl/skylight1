package net.nycjava.skylight.service;

public interface SteadinessObserver {
	/**
	 * Called intermittently to notify the observer of the steadiness of the device. Forces are measured in centimeters
	 * per second squared and angles in degrees.
	 */
	void steadinessNotification(float paremetersThatICantThinkOfRightNow);
}
