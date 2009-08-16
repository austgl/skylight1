package net.nycjava.skylight.service;

public interface BalancedObjectObserver {
	/**
	 * ???
	 */
	void balancedObjectNotification(float anX, float aY);

	/**
	 * ???
	 */
	void fallenOverNotification();
}
