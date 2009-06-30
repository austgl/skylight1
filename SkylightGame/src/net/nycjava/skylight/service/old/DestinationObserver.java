package net.nycjava.skylight.service.old;

public interface DestinationObserver {
	/**
	 * Called intermittently to notify the observer of the current position relative to some destination. The position
	 * is reported independently of the device's orientation. Distances are measured in centimeters and angles in
	 * degrees.
	 */
	void destinationNotification(float anAngle, float aDistance);
}
