package net.nycjava.skylight.service;

public interface BalancedObjectObserver {
	/**
	 * ???
	 */
	void balancedObjectNotification(float aDirectionOfFallingInRadians, float anAngleOfLeanInRadians);

	/**
	 * ???
	 */
	void fallenOverNotification();
}
