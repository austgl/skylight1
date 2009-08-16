package net.nycjava.skylight.service.old;

/**
 * Allows for notification of changes in position.
 */
public interface PositionObserver {
	/**
	 * Called intermittently to notify the observer of the current position. The position is reported independently of
	 * the device's orientation. Positions are measured in centimeter and are relative to the point at which the
	 * observer first started observing.
	 * @param aPosition TODO
	 */
	void positionNotification(Position aPosition);
}
